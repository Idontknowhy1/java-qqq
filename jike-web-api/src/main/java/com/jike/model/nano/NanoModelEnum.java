package com.jike.model.nano;

public enum NanoModelEnum {

    BANNER("nano-banana", "b"),
    BANNER2("banana2", "b2"),
    GEMINI_2_5_FLASH("gemini-2.5-flash-image", "gm25"),

    HP("nano-banana_hp", "hp"),
    MULTIPLE_SCENE("multiple_scene", "ms"),

    VIDEO_HP("video-hp", "video-hp"),

    SORA2("sora-2", "so2"),
    SORA2_PRO("sora-2-pro", "so2p"),
    VEO3_PRO("veo3-pro","veo3p"),
    VEO3_PRO_FRAMES("veo3-pro-frames","veo3pfs"),
    VEO3_FAST_FRAMES("veo3-fast-frames","veo3ffs"),
    VEO3_FRAMES("veo3-frames","veo3fs"),
    VEO3_1_FAST("veo3.1-fast","veo3f");

    final String code;
    final String code2;

    NanoModelEnum(String code, String code2) {
        this.code = code;
        this.code2 = code2;
    }

    public String toCode() {
        return this.code;
    }
    public String toCode2() {
        return this.code2;
    }

}
