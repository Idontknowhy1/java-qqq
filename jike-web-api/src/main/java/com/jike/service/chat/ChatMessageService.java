package com.jike.service.chat;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jike.redis.RedisCacheUtil;
import com.jjs.common.PageListResult;
import com.jike.model.chat.Convertor_ChatMessage;
import com.jike.mapper.chat.ChatMessageMapper;
import com.jike.model.chat.ChatMessageEntity;
import com.jike.model.chat.ChatMessageVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ChatMessageService extends ServiceImpl<BaseMapper<ChatMessageEntity>, ChatMessageEntity> implements IService<ChatMessageEntity> {

    final ChatMessageMapper mapper;
    public ChatMessageService(ChatMessageMapper mapper) { this.mapper = mapper; }

    @Autowired
    ChatMessageMapper chatMessageMapper;

    public PageListResult<ChatMessageVO> getPageList(Page<ChatMessageEntity> page, Wrapper<ChatMessageEntity> queryWrapper) throws Exception {
        Page<ChatMessageEntity> resultPage = mapper.selectPage(page, queryWrapper);
        List<ChatMessageEntity> list = resultPage.getRecords();
        List<ChatMessageVO> voList = Convertor_ChatMessage.INSTANCE.convertToVoList(list);
        PageListResult<ChatMessageVO> pageResult = new PageListResult<>();
        pageResult.setTotalCount(resultPage.getTotal());
        pageResult.setPageNum(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setList(voList);
        return pageResult;
    }
    public List<ChatMessageVO> getList(Wrapper<ChatMessageEntity> queryWrapper) throws Exception {
        List<ChatMessageEntity> list = mapper.selectList(queryWrapper);
        return Convertor_ChatMessage.INSTANCE.convertToVoList(list);
    }

    /**
     * 生成过程中，缓存
     */
    public void saveStreamSlice(String jsonResponse, String userId, String convId) {
        try {
            String redisKey = "ai:user:chating:" + userId + ":" + convId;
            String chating = RedisCacheUtil.getString(redisKey);
            if (chating == null) {
                chating = "";
                RedisCacheUtil.setString(redisKey, chating);
            }

            String text = parseStreamSlice(jsonResponse);
            RedisCacheUtil.setString(redisKey, chating + text);

        } catch (Exception e) {
        }
    }

    public String parseStreamSlice(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    String text = parts.get(0).path("text").asText();
                    return text;
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    public void saveMsg(String content, String userId, String convId, int direction) {
        // 入库
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setContent(content);
        entity.setConvId(Integer.parseInt(convId));
        entity.setUserId(Long.parseLong(userId));
        entity.setDirection(direction);
        chatMessageMapper.insert(entity);

        String completeRedisKey = "ai:user:chat:" + userId + ":" + convId;
        List<ChatMsg> list = RedisCacheUtil.getList(completeRedisKey, ChatMsg.class);
        if (list == null || list.isEmpty()) {
            list = new ArrayList<>();
        }

        ChatMsg chatMsg = new ChatMsg();
        chatMsg.content = content;
        chatMsg.direction = direction;
        chatMsg.setId(entity.getId());
        list.add(chatMsg);

        RedisCacheUtil.del(completeRedisKey);
        RedisCacheUtil.setList(completeRedisKey, list);
    }

    /**
     * 生成完成后入库
     */
    public void saveCompleteResponse(String userId, String convId) {
        String redisKey = "ai:user:chating:" + userId + ":" + convId;
        String chating = RedisCacheUtil.getString(redisKey);
        if (chating == null) {
            return;
        }
        saveMsg(chating, userId, convId, 2);
        RedisCacheUtil.del(redisKey);
    }

    public List<String> getHistoryMessages(String userId, String convId) {
        String redisKey = "ai:user:chat:" + userId + ":" + convId;
        List<ChatMsg> list = RedisCacheUtil.getList(redisKey, ChatMsg.class);
        if (list == null) {
            return new ArrayList<>();
        }
        List<ChatMsg> historyMessages = new ArrayList<>();
        if (list.size() > 6) {
            historyMessages = list.subList(list.size() - 6, list.size());
        } else {
            historyMessages = list;
        }
        List<String> result = new ArrayList<>();
        for (ChatMsg chatMsg : historyMessages) {
            result.add(chatMsg.getContent());
        }
        return result;
    }

    // 获取所有消息
    public List<ChatMsg> getAllHistoryMessages(long userId, int convId) {
        String redisKey = "ai:user:chat:" + userId + ":" + convId;
        List<ChatMsg> list = RedisCacheUtil.getList(redisKey, ChatMsg.class);
        if (list != null && list.size() > 0) {
            return list;
        }

        List<ChatMessageEntity> chatMessageEntities = chatMessageMapper.selectList(new QueryWrapper<ChatMessageEntity>().lambda()
                .eq(ChatMessageEntity::getUserId, userId)
                .eq(ChatMessageEntity::getConvId, convId)
                .orderBy(true, true, ChatMessageEntity::getCreateTime)
        );
        List<ChatMsg> result = Convertor_ChatMessage.INSTANCE.convertToMsgList(chatMessageEntities);
        RedisCacheUtil.del(redisKey);
        RedisCacheUtil.setList(redisKey,result);
        return result;
    }

    @Data
    public static class  ChatMsg {
        private int id;
        private String content;
        private Integer direction;
    }
}

