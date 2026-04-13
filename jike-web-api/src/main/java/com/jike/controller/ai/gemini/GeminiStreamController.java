package com.jike.controller.ai.gemini;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jike.mapper.chat.GenConfigMapper;
import com.jike.model.chat.GenConfigEntity;
import com.jike.model.chat.GenConfigVO;
import com.jike.redis.RedisCacheUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/gemini")
public class GeminiStreamController {
    private final GeminiService geminiService;
    @Autowired
    GenConfigMapper genConfigMapper;

    public GeminiStreamController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamResponsePost(@RequestBody ChatRequest request) {
        String userId = "1212";
        String instruction = """
                你是一名古风历史正剧编剧。台词需符合时代语感，兼顾文言和白话；详写服饰、礼仪、建筑细节；强调权谋斗争的张力，通过微表情和潜台词呈现。
                
                【交互规则】
                1. 你现在处于对话模式，请记住之前的剧情上下文。
                2. 如果用户发送小说原文，请直接进行剧本改编。
                3. 如果用户提出修改意见（如“更刺激一点”），请根据上下文重新修改上一段剧本。
                4. 始终保持古风历史的风格调性。
                """;
        if (request.getStyle() != null && !request.getStyle().isEmpty()) {
            GenConfigEntity genConfigEntity = genConfigMapper.selectOne(new QueryWrapper<GenConfigEntity>().lambda().eq(GenConfigEntity::getType, request.getStyle()));
            if (genConfigEntity != null) {
                instruction = genConfigEntity.getInstruction();
            }
        }

        return geminiService.streamGenerateContent(request.getMessage(), instruction, getHistoryMessages(userId))
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
        private String style;
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
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    String text = parts.get(0).path("text").asText();
                    RedisCacheUtil.setString(redisKey, chating + text);
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
        String redisKey = "ai:user:chat:" + userId;
        List<String> list = RedisCacheUtil.getList(redisKey);
        if (list == null) {
            return new ArrayList<>();
        }
        if (list.size() <= 6) {
            return list;
        }
        return list.subList(list.size() - 7, list.size() - 1);
    }

}
