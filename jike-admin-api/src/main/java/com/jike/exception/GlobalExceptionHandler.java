package com.jike.exception;

import cn.dev33.satoken.exception.NotPermissionException;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 专门处理 @RequestBody 参数校验失败抛出的 MethodArgumentNotValidException 异常
     * 这正是你遇到的异常类型
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        // 从异常中提取所有字段错误信息
//        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
//                .map(error -> error.getField() + ": " + error.getDefaultMessage())
//                .collect(Collectors.joining("; "));
        String name = ex.getBindingResult().getFieldError().getField();
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
//        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
                //.stream()
//                .map(error -> error.getField() + ": " + error.getDefaultMessage()).toList().get(0);

        return ApiResponseUtil.getApiResponseParamsError(name  + message);
    }

    @ExceptionHandler(NotPermissionException.class)
    public ApiResponse handleNotPermissionException(NotPermissionException e) {
        return ApiResponseUtil.getLackPermissionResponse("缺少权限：\"" + e.getPermission()+ "\"");
    }

//    @ExceptionHandler(Exception.class)
//    public ApiResponse handleOtherExceptions(Exception ex) throws Exception{
//        throw  ex;
////        return ApiResponseUtil.getApiResponseFailure("服务器内部错误");
//    }

}
