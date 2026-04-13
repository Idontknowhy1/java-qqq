package com.jjs.response;

public class ApiResponseUtil {

    /**
     * 成功
     * @return
     */
    public static ApiResponse getApiResponseSuccess(){
        return createApiResponse(ApiResponseEnum.SUCCESS.getCode(),ApiResponseEnum.SUCCESS.getMsg(),null);
    }
    public static ApiResponse getApiResponseSuccess(Object data){
        return ApiResponseUtil.createApiResponse(ApiResponseEnum.SUCCESS.getCode(),ApiResponseEnum.SUCCESS.getMsg(),data);
    }
    public static ApiResponse getApiResponseSuccess(String msg,Object data){
        return ApiResponseUtil.createApiResponse( ApiResponseEnum.SUCCESS.getCode(), msg, data);
    }

    /**
     * 失败
     * @return
     */
    public static ApiResponse getApiResponseFailure(){
        return ApiResponseUtil.createApiResponse(
                ApiResponseEnum.FAIL.getCode(),
                ApiResponseEnum.FAIL.getMsg(),
                null);
    }
    public static ApiResponse getApiResponseFailure(String msg){
        return ApiResponseUtil.createApiResponse(
                ApiResponseEnum.FAIL.getCode(),
                msg,
                null);
    }
    //用户id无效
    public static ApiResponse getUserNoInvalidResponse(){
        return ApiResponseUtil.createApiResponse(
                ApiResponseEnum.USER_NO_IVALID.getCode(),
                ApiResponseEnum.USER_NO_IVALID.getMsg(),
                null);
    }

    /**
     * 认证失败
     * @return
     */
    public static ApiResponse getApiResponseAuthFailure(String msg){
        return ApiResponseUtil.createApiResponse(
                ApiResponseEnum.AUTH_FAIL.getCode(),
                msg,
                null);
    }

    /**
     * 参数缺失
     * @return
     */
    public static ApiResponse getApiResponseParamsMissing(String msg){
        return ApiResponseUtil.createApiResponse(
                ApiResponseEnum.PARAM_MISSING.getCode(),
                msg,
                null);
    }
    /**
     * 参数错误(参数与预先设定值不一致)
     * @return
     */
    public static ApiResponse getApiResponseParamsError(String msg){
        return ApiResponseUtil.createApiResponse(
                ApiResponseEnum.PARAM_ERROR.getCode(),
                msg,
                null);
    }
    /**
     * 方法不被允许
     * @return
     */
    public static ApiResponse getApiResponseMethodNotAllowed(){
        return ApiResponseUtil.createApiResponse(
                ApiResponseEnum.METHOD_NOT_ALLOWED.getCode(),
                ApiResponseEnum.METHOD_NOT_ALLOWED.getMsg(),
                null);
    }

    /**
     * 服务器开小差，请稍后再试
     * @return
     */
    public static ApiResponse getApiResponseBusy(){
        return ApiResponseUtil.createApiResponse(
                ApiResponseEnum.BUSY.getCode(),
                ApiResponseEnum.BUSY.getMsg(),
                null);
    }

    /**
     * 操作频率过高
     * @return
     */
    public static ApiResponse getFrequencyTooHeightResponse(){
        return ApiResponseUtil.createApiResponse(
                ApiResponseEnum.FREQUENCY_TOO_HIGH.getCode(),
                ApiResponseEnum.FREQUENCY_TOO_HIGH.getMsg(),
                null);
    }

    /**
     * 积分不足
     * @return
     */
    public static ApiResponse getCoinNotEnoughtResponse(){
        return ApiResponseUtil.createApiResponse(
                ApiResponseEnum.COIN_NOT_ENOUGHT.getCode(),
                ApiResponseEnum.COIN_NOT_ENOUGHT.getMsg(),
                null);
    }

    /**
     * 权限不足
     */
    public static ApiResponse getLackPermissionResponse(){
        return ApiResponseUtil.createApiResponse(
                ApiResponseEnum.LACK_PERMISSION.getCode(),
                ApiResponseEnum.LACK_PERMISSION.getMsg(),
                null);
    }
    /**
     * 权限不足
     */
    public static ApiResponse getLackPermissionResponse(String msg){
        return ApiResponseUtil.createApiResponse(
                ApiResponseEnum.LACK_PERMISSION.getCode(),
                msg,
                null);
    }


    public static ApiResponse createApiResponse(Integer code,String msg,Object data){
        ApiResponse response = new ApiResponse();
        response.setCode(code);
        response.setMsg(msg);
        response.setData(data);
        return response;
    }
}