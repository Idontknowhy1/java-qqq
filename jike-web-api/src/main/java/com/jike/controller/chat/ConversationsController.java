package com.jike.controller.chat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jike.controller.ai.gemini.GeminiService;
import com.jike.controller.chat.request.ConversationSendMsgRequest;
import com.jike.mapper.chat.GenConfigMapper;
import com.jike.model.chat.*;
import com.jike.request.AppRequestHeader;
import com.jike.service.chat.ChatMessageService;
import com.jjs.common.ConvertUtil;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.chat.ConversationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RequestMapping("/conversations/v1")
@RestController
@Slf4j
public class ConversationsController {

    final
    ConversationsService service;
    public ConversationsController(ConversationsService service) { this.service = service; }

    @Autowired
    GenConfigMapper genConfigMapper;
    @Autowired
    ChatMessageService chatMessageService;
    @Autowired
    GeminiService geminiService;

    /**
      * 保存、修改
     */
    @PostMapping("/create")
    public ApiResponse create(@Validated @RequestBody ConversationsDTO obj, AppRequestHeader header) throws Exception {

        if (!header.isValid()) {
            return ApiResponseUtil.getApiResponseAuthFailure("请登录");
        }

        ConversationsEntity entity = ConvertUtil.convertTo(obj, ConversationsEntity.class);
        entity.setUserId(Long.parseLong(header.getUserId()));
        if (obj.getType().equals("CUSTOM")) {
            entity.setIcon("edit");
        } else {
            GenConfigEntity genConfigEntity = genConfigMapper.selectOne(new QueryWrapper<GenConfigEntity>().lambda().eq(GenConfigEntity::getType, obj.getType()));
            if (genConfigEntity == null) {
                return ApiResponseUtil.getApiResponseParamsError("type无效");
            }
            entity.setInstruction(genConfigEntity.getInstruction());
            entity.setIcon(genConfigEntity.getIcon());
        }

        service.save(entity);
        entity = service.getById(entity.getId());
        ConversationsVO vo = Convertor_Conversations.INSTANCE.convertToVo(entity);

        return ApiResponseUtil.getApiResponseSuccess("保存成功", vo);
    }

//    @PostMapping("/update")
//    public ApiResponse update(@RequestBody ConversationsDTO obj) throws Exception {
//        ConversationsEntity entity = ConvertUtil.convertTo(obj, ConversationsEntity.class);
//        service.updateById(entity);
//        entity = service.getById(entity.getId());
//        ConversationsVO vo = ConvertUtil.convertTo(entity, ConversationsVO.class);
//        return ApiResponseUtil.getApiResponseSuccess("保存成功", vo);
//    }

    /**
     * 删除
     */
    @GetMapping("/delete")
    public ApiResponse delete(@RequestParam String id) throws Exception {
        try {
            service.removeById(id);
            return ApiResponseUtil.getApiResponseSuccess("删除成功",null);
        }catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
           return ApiResponseUtil.getApiResponseFailure();
       }
    }

    /**
     * 获取列表
     */
    @GetMapping("/list")
    public ApiResponse getList(AppRequestHeader header) throws Exception {
        try {
            if (!header.isValid()) {
                return ApiResponseUtil.getApiResponseAuthFailure("请登录");
            }
            List<ConversationsVO> result = service.getList(new QueryWrapper<ConversationsEntity>().lambda()
                    .eq(ConversationsEntity::getUserId, header.getUserId())
                    .orderBy(true, false, ConversationsEntity::getUpdateTime));
            return ApiResponseUtil.getApiResponseSuccess(result);

        } catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 发送消息
     */
    @PostMapping(value = "/sendMsg", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamResponsePost(@Validated @RequestBody ConversationSendMsgRequest dto, AppRequestHeader header) {
        if (!header.isValid()) {
            return customStreamResponse("error", "请登录");
        }
        ConversationsEntity conversationsEntity = service.getOne(new QueryWrapper<ConversationsEntity>().lambda().eq(ConversationsEntity::getId, dto.getConvId()));
        if (conversationsEntity == null) {
            return customStreamResponse("error", "convId 无效");
        }

        String userId = header.getUserId();

        String instruction = conversationsEntity.getInstruction();
        String convId = String.valueOf(conversationsEntity.getId());

        // 入库
        chatMessageService.saveMsg(dto.getContent(), userId, convId, 1);

        return geminiService.streamGenerateContent(dto.getContent(),
                        instruction,
                        chatMessageService.getHistoryMessages(userId, convId))
                .map(text -> {
//                    String plainText = chatMessageService.parseStreamSlice(text);
                    return ServerSentEvent.<String>builder()
                            .data(text)
                            .build();
                })
                .doOnNext(text -> {
                    if (text.data() != null && !text.data().isEmpty()) {
                        chatMessageService.saveStreamSlice(text.data(), userId, convId);
                    }
                })
                .doOnComplete(() -> {
                    // 消息入库
                    chatMessageService.saveCompleteResponse(userId, convId);
                    // 更新会话时间
                    service.update(new UpdateWrapper<ConversationsEntity>().lambda()
                            .set(ConversationsEntity::getUpdateTime, System.currentTimeMillis() / 1000)
                            .eq(ConversationsEntity::getId, convId));
                })
                .concatWithValues(ServerSentEvent.<String>builder()
                        .data("[DONE]")
                        .event("done")
                        .build());
    }

    /**
     * 获取会话下的消息
     */
    @GetMapping("/messages")
    public ApiResponse getMessageList(@RequestParam String convId) throws Exception {
        try {
            ConversationsEntity conversationsEntity = service.getOne(new QueryWrapper<ConversationsEntity>().lambda().eq(ConversationsEntity::getId, convId));
            if (conversationsEntity == null) {
                return ApiResponseUtil.getApiResponseParamsError("convId 无效");
            }
            List<ChatMessageService.ChatMsg> allHistoryMessages = chatMessageService.getAllHistoryMessages(conversationsEntity.getUserId(),
                    conversationsEntity.getId());
            return ApiResponseUtil.getApiResponseSuccess(allHistoryMessages);

        } catch (Exception ex) {
            log.error(this.getClass().getName() + " error" + ex, ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }


    public Flux<ServerSentEvent<String>> customStreamResponse(String event, String msg) {
        // 创建Sink作为事件源
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().unicast().onBackpressureBuffer();

        // 在异步任务中处理业务逻辑并手动控制流的结束
        Mono.fromRunnable(() -> {
            try {
                // 认证失败：发送错误消息并立即结束流
                ServerSentEvent<String> errorEvent = ServerSentEvent.<String>builder()
                        .event(event)
                        .data(msg)
                        .build();
                sink.tryEmitNext(errorEvent);
                sink.tryEmitComplete(); // 关键步骤：发送完成信号，关闭连接

            } catch (Exception e) {
                // 4. 如果发生异常，发送错误信号并结束流
                sink.tryEmitError(e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).subscribe(); // 使用Mono.fromRunnable和subscribe确保异步执行

        // 返回给前端订阅的Flux
        return sink.asFlux();
    }
}

