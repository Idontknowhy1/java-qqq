package com.jike.controller.ai.gemini;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeminiRequest {
    private SystemInstruction systemInstruction; // 新增：系统指令
    private List<Content> contents; // 保留：内容部分
    private GenerationConfig generationConfig; // 新增：生成配置
    private List<String> responseModalities; // 新增：响应模态

    // 内部静态类：系统指令
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SystemInstruction {
        private List<Part> parts;
    }

    // 内部静态类：生成配置（包含思考配置）
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GenerationConfig {
        private double temperature = 1.0; // 默认值参考JS对象
        private double topP = 1.0;
        private int maxOutputTokens = 65536;
        private ThinkingConfig thinkingConfig; //= new ThinkingConfig(false, 0); // 嵌套配置

        // 内部静态类：思考配置
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ThinkingConfig {
            private boolean includeThoughts;
            private int thinkingBudget;
        }
    }

    // 保留原有的Content和Part类结构
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Content {
        private List<Part> parts;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Part {
        private String text;
    }
}
