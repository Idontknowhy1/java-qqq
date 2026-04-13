package com.jike.controller.ai.apimart;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jike.controller.ai.gemini.GeminiService;
import com.jike.redis.RedisCacheUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/mart")
public class ApiMartStreamController {

    @Autowired
    ApiMartService apiMartService;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamResponsePost(@RequestBody ChatRequest request) throws IOException, InterruptedException {
        String userId = "1212";

        String instruction = """
                你是一名古风历史正剧编剧。台词需符合时代语感，兼顾文言和白话；详写服饰、礼仪、建筑细节；强调权谋斗争的张力，通过微表情和潜台词呈现。
                
                【交互规则】
                1. 你现在处于对话模式，请记住之前的剧情上下文。
                2. 如果用户发送小说原文，请直接进行剧本改编。
                3. 如果用户提出修改意见（如“更刺激一点”），请根据上下文重新修改上一段剧本。
                4. 始终保持古风历史的风格调性。
                """;

        return apiMartService.streamGenerateContent(request.getMessage(), instruction, getHistoryMessages(userId))
                .map(text -> ServerSentEvent.<String>builder()
                        .data(text)
                        .build())
                .doOnNext(text -> {
                    if (text.data() != null && !text.data().isEmpty()) {
                        saveStreamSlice(text.data(), userId);
                    }
                })
                .doOnComplete(() -> saveCompleteResponse(userId))
                .concatWithValues(ServerSentEvent.<String>builder()
                        .data("[DONE]")
                        .event("done")
                        .build());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRequest {
        private String message;
    }

    /**
     * 生成过程中，缓存
     * @param userId
     */
    private void saveStreamSlice(String jsonResponse, String userId) {
        try {
            String redisKey = "ai:user:chating:" + userId;
            String chating = RedisCacheUtil.getString(redisKey);
            if (chating == null) {
                chating = "";
                RedisCacheUtil.setString(redisKey, chating);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode delta = choices.get(0).path("delta");
                String content = delta.path("content").asText();
                if (content != null && !content.isEmpty()) {
//                    log.debug("----收到提示词: " + content);
                    RedisCacheUtil.setString(redisKey, chating + content);
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * 生成完成后入库
     * @param userId
     */
    private void saveCompleteResponse(String userId) {
        String redisKey = "ai:user:chating:" + userId;
        String chating = RedisCacheUtil.getString(redisKey);
        if (chating == null) {
            return;
        }

        String completeRedisKey = "ai:user:chat:" + userId;
        List<String> list = RedisCacheUtil.getList(completeRedisKey);
        if (list == null || list.isEmpty()) {
            list = new ArrayList<>();
            list.add(chating);
        }
        RedisCacheUtil.setList(completeRedisKey, list);
        RedisCacheUtil.del(redisKey);
    }

    private List<String> getHistoryMessages(String userId) {
//        String redisKey = "ai:user:chat:" + userId;
//        List<String> list = RedisCacheUtil.getList(redisKey);
//        if (list == null) {
//            return new ArrayList<>();
//        }
//        if (list.size() <= 6) {
//            return list;
//        }
//        return list.subList(list.size() - 7, list.size() - 1);
        return new ArrayList<>();
    }

}
