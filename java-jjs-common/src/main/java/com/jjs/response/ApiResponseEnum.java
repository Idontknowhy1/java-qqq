package com.jjs.response;

public enum ApiResponseEnum {
    /**
     * 成功
     */
    SUCCESS(10000,"成功"),

    /**
     * 系统错误
     */
    FAIL(99999,"系统错误"),

    /**
     * 权限不足
     */
    LACK_PERMISSION(403,"权限不足"),

    /**
     * 参数缺失
     */
    PARAM_MISSING(10001,"参数缺失"),

    /**
     * 参数错误(参数与预先设定值不一致)
     */
    PARAM_ERROR(10001,"参数错误"),
    /**
     * 服务器开小差，请稍后再试
     */
    BUSY(10002,"服务器开小差，请稍后再试"),
    /**
     * 认证失败
     */
    AUTH_FAIL(10003,"认证失败"),
    /**
     * 该方法不被运行
     */
    METHOD_NOT_ALLOWED(10004,"该方法不被允许"),
    /**
     * 操作频率过高
     */
    FREQUENCY_TOO_HIGH(10005,"操作频率过高，请稍后再试"),
    /**
     * 用户No无效
     */
    USER_NO_IVALID(10013,"用户Id无效"),

    /**
     * 积分不足
     */
    COIN_NOT_ENOUGHT(10014,"积分不足"),
    ;

    private Integer code = 0;
    private String msg;
    private ApiResponseEnum(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

}
