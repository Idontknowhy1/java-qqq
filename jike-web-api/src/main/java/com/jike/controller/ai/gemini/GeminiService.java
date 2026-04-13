package com.jike.controller.ai.gemini;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
public class GeminiService {
    private final WebClient webClient;
    private final String geminiApiKey = "sk-1am9qZPq30YrsVfZQGf4IIz4vkG5GBEVyOSirpkvb28lLZvI";
    private final String model = "gemini-3-pro-preview";

    public GeminiService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.vectorengine.ai")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Flux<String> streamGenerateContent(String prompt, String instruction, List<String> historyMessages) {
        String url = String.format("/v1beta/models/%s:generateContent?key=%s&alt=sse", model, geminiApiKey);

        GeminiRequest payload = buildPayload(prompt, instruction, historyMessages);

        return webClient.post()
                .uri(url)
                .bodyValue(payload)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(text -> !text.trim().isEmpty());
//                .map(this::extractContentFromResponse)
//                .filter(content -> content != null && !content.isEmpty());
    }

    /**
     * 根据新结构构建请求体
     * 注意：您需要根据实际业务逻辑实现buildContents方法
     */
    private GeminiRequest buildPayload(String prompt, String systemInstruction, List<String> historyMessages) {
        GeminiRequest request = new GeminiRequest();

        // 设置系统指令
        if (systemInstruction != null && !systemInstruction.isEmpty()) {
            GeminiRequest.SystemInstruction sysInst = new GeminiRequest.SystemInstruction();
            sysInst.setParts(List.of(new GeminiRequest.Part(systemInstruction)));
            request.setSystemInstruction(sysInst);
        }

        // 设置对话内容（需要您实现buildContents方法）
        // 假设buildContents方法返回List<GeminiRequest.Content>
        request.setContents(buildContents(historyMessages, "", prompt));

        // 设置生成配置（使用内部类默认值）
        GeminiRequest.GenerationConfig genConfig = new GeminiRequest.GenerationConfig();
        request.setGenerationConfig(genConfig);

        // 设置响应模态
        request.setResponseModalities(List.of("TEXT"));

        return request;
    }

    /**
     * 需要您根据业务逻辑实现的方法
     * 将历史消息和当前输入构建成Content列表
     */
    private List<GeminiRequest.Content> buildContents(List<String> historyMessages, String currentContext, String input) {
        // 这里需要您根据实际业务逻辑实现
        // 示例：简单地将当前输入作为新的内容
        GeminiRequest.Part part = new GeminiRequest.Part(input);
        GeminiRequest.Content content = new GeminiRequest.Content(List.of(part));
        return List.of(content);
    }
}
