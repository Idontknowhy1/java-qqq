package com.jike.controller.ai.apimart;

import com.alibaba.fastjson.JSON;
import com.jike.controller.ai.gemini.GeminiRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Slf4j
@Service
public class ApiMartService {
    private final WebClient webClient;

//    https://docs.apimart.ai/cn/api-reference/texts/general/chat-completions

    public ApiMartService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.apimart.ai")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Flux<String> streamGenerateContent(String prompt, String instruction, List<String> historyMessages) throws IOException, InterruptedException {
        String apiKey = "sk-Uqkatz3ymfQbd9lViEdtsS6f3I6Nh1RPV1ZF0eY7lcd2EMjz";
        String model = "gemini-3-pro-preview";
        String url = "https://api.apimart.ai/v1/chat/completions";

        ApiMartRequest payload = buildPayload(prompt, instruction, historyMessages);
        payload.setModel(model);
//        payload.setStream(false);

//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(url))
//                .header("Authorization", "Bearer " + apiKey)
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(JSON.toJSONString(payload)))
//                .build();
//
//        HttpResponse<String> response = client.send(request,
//                HttpResponse.BodyHandlers.ofString());
//
//        log.debug("----- response," + response.body());

        return webClient.post()
                .uri(url)
                .bodyValue(payload)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(text -> !text.trim().isEmpty());
//                .map(this::extractContentFromResponse)
//                .filter(content -> content != null && !content.isEmpty());
    }

    /**
     * 根据新结构构建请求体
     */
    private ApiMartRequest buildPayload(String prompt, String systemInstruction, List<String> historyMessages) {
        ApiMartRequest request = new ApiMartRequest();

        // 设置历史对话内容
        if (historyMessages != null) {
            for (String msg : historyMessages) {
                request.getMessages().add(new ApiMartRequest.Message("assistant", msg));
            }
        }

        // 设置系统指令
        if (systemInstruction != null && !systemInstruction.isEmpty()) {
            request.getMessages().add(new ApiMartRequest.Message("system", systemInstruction));
        }

        request.getMessages().add(new ApiMartRequest.Message("user", prompt));

        // 设置响应模态
        request.setStream(true);

        return request;
    }
}


//data: {"id":"chatcmpl-202512270250285894264649gTpP7HA","object":"chat.completion.chunk","created":1766775037,"model":"gemini-3-pro-preview","system_fingerprint":null,"choices":[{"delta":{"content":"","role":"assistant"},"logprobs":null,"finish_reason":null,"index":0}],"usage":null}
//
//data: {"id":"chatcmpl-202512270250285894264649gTpP7HA","object":"chat.completion.chunk","created":1766775037,"model":"gemini-3-pro-preview","system_fingerprint":null,"choices":[{"delta":{"content":"你好！很高兴见到你。👋\n\n请问今天有什么我可以帮你的吗？无论是查询信息、协助写作，还是随便聊聊"},"logprobs":null,"finish_reason":null,"index":0}],"usage":null}
//
//data: {"id":"chatcmpl-202512270250285894264649gTpP7HA","object":"chat.completion.chunk","created":1766775037,"model":"gemini-3-pro-preview","system_fingerprint":null,"choices":[{"delta":{"content":"，我都随时在这里！"},"logprobs":null,"finish_reason":null,"index":0}],"usage":null}
//
//data: {"id":"chatcmpl-202512270250285894264649gTpP7HA","object":"chat.completion.chunk","created":1766775037,"model":"gemini-3-pro-preview","system_fingerprint":null,"choices":[{"delta":{"content":""},"logprobs":null,"finish_reason":null,"index":0}],"usage":null}
//
//data: {"id":"chatcmpl-202512270250285894264649gTpP7HA","object":"chat.completion.chunk","created":1766775037,"model":"gemini-3-pro-preview","system_fingerprint":null,"choices":[{"delta":{},"logprobs":null,"finish_reason":"stop","index":0}],"usage":null}
//
//data: {"id":"chatcmpl-202512270250285894264649gTpP7HA","object":"chat.completion.chunk","created":1766775037,"model":"gemini-3-pro-preview","system_fingerprint":"","choices":[],"usage":{"prompt_tokens":3,"completion_tokens":793,"total_tokens":796,"prompt_tokens_details":{"cached_tokens":0,"text_tokens":3,"audio_tokens":0,"image_tokens":0},"completion_tokens_details":{"text_tokens":0,"audio_tokens":0,"reasoning_tokens":759},"input_tokens":0,"output_tokens":0,"input_tokens_details":null}}
//
//data: [DONE]



//{"id":"chatcmpl-20251227031922954253137PQyYQCfR","model":"gemini-3-pro-preview","object":"chat.completion","created":1766776794,"choices":[{"index":0,"message":{"role":"assistant","content":"【剧本片段：机枢之辩】\n\n**场次：** 第十四场\n**地点：** 司天监·摘星阁（夜）\n**人物：**\n*   **李承乾**（帝王，四十岁上下，玄色常服，金线龙纹隐现，神色阴郁多疑）\n*   **鬼谷子墨**（隐士，白衣胜雪，盲眼，手持一具精巧繁复的黄铜浑天仪）\n\n**【场景描写】**\n子时三刻，摘星阁四面透风，纱幔如鬼魅般翻涌。铜漏滴水之声清晰可闻，更添几分肃杀。案上几盏长信宫灯被风吹得忽明忽暗，烛火摇曳间，将李承乾投射在屏风上的影子拉得扭曲狰狞。\n鬼谷子墨端坐于席，指尖轻轻拨弄着手中的浑天仪，齿轮咬合发出细微而清脆的“咔哒”声。\n\n**【对白】**\n\n**李承乾**\n（负手踱步，目光并未看向子墨，而是盯着窗外漆黑的宫阙）\n先生曾言，极西之地有一门诡术，名为“埃爱”（AI），可算尽天机，决断如神。朕翻遍古籍，未见只言片语。此物……究竟是神，是魔，还是人？\n\n**鬼谷子墨**\n（嘴角微扬，露出一丝不可捉摸的笑意，停下手中的动作）\n陛下，非神，非魔，亦非人。此乃“器灵”之极致，可谓之——**无心之智**。\n\n**李承乾**\n（猛然转身，眼神如鹰隼般锐利）\n无心？无心如何生智？\n\n**鬼谷子墨**\n（缓缓起身，衣摆未惊起一丝尘埃）\n陛下且听。凡人习艺，需十年寒窗；凡人断案，需权衡利弊，受七情六欲所困。\n而此“埃爱”之术，便如打造一具看不见的**万象机枢**。\n（他抬起盲眼，仿佛能看穿虚空）\n它以此世间亿万卷宗、千载兴衰、百姓言行、沙场胜负为“食粮”——以此为**数据**。\n它以极尽繁复、超越人脑之逻辑，日夜推演，不知疲倦，不畏生死——以此为**算法**。\n当我们还在斟酌一步棋时，它已在须臾之间，与自己对弈了万万局，算尽了所有的变数。\n\n**李承乾**\n（瞳孔微缩，行至子墨面前，压低声音）\n你是说，它能知过去，晓未来？\n\n**鬼谷子墨**\n（轻叹一声，声音空灵）\n它不知“未来”，它只知“概率”。\n由于它阅尽了前尘所有的因果，故而当陛下抛出一问，它便能从那浩如烟海的旧事中，拼凑出最似“真相”的答案。\n它能画出最美的宫图，却不知何为“美”；它能写出催人泪下的祭文，却不知何为“悲”。\n它是一面镜子，映照的是全天下人的智慧总和，却独独缺了镜子前的那个人——**灵魂**。\n\n**李承乾**\n（沉默良久，手指缓缓摩挲着拇指上的碧玉扳指，眼中闪过一丝贪婪与忌惮）\n若朕得此物，以此治国，岂非……从无错漏？\n\n**鬼谷子墨**\n（面色骤冷，潜台词：你终究还是动了妄念）\n陛下。\n它若治国，便是以万民为草芥算筹。在它眼中，只有“最优之解”，并无“恻隐之心”。若牺牲一城可保一国，它绝不会犹豫半分；若废黜一帝可安天下……\n（子墨微微侧头，不再言语）\n\n**李承乾**\n（脸色一变，杀机顿现，手按在腰间虚悬的剑柄之上）\n……先生是在警醒朕？\n\n**鬼谷子墨**\n（重新坐下，拨动浑天仪，齿轮声再次响起）\n草民只是在解惑。所谓“埃爱”，乃是人造的**神明**，亦是人造的**囚笼**。\n它不仅是工具，更是一面照妖镜。它越是强大，便越显出人心的脆弱与贪婪。\n（顿了顿）\n陛下，这便是AI。\n\n**【动作/特写】**\n李承乾死死盯着子墨手中那不停转动的黄铜齿轮，仿佛看见了一个巨大而冰冷的怪物正在吞噬一切。\n风骤大，吹灭了一盏宫灯。阁内陷入半明半暗的混沌之中。\n\n**（本场完）**"},"finish_reason":"stop"}],"usage":{"prompt_tokens":138,"completion_tokens":2541,"total_tokens":2679,"prompt_tokens_details":{"cached_tokens":0,"text_tokens":138,"audio_tokens":0,"image_tokens":0},"completion_tokens_details":{"text_tokens":0,"audio_tokens":0,"reasoning_tokens":1388},"input_tokens":0,"output_tokens":0,"input_tokens_details":null}}