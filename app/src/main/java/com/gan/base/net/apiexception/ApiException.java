package com.gan.base.net.apiexception;

/**
 * Created by gan on 16/3/10.
 */
public class ApiException extends RuntimeException {
    public static final int errData = 0;
    public static final int USER_NOT_EXIST = 100;
    public static final int WRONG_PASSWORD = 101;


    public ApiException(int resultCode) {
        this(getApiExceptionMessage(resultCode));
    }

    public ApiException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     * @param code
     * @return
     */
    private static String getApiExceptionMessage(int code){
        String message = "";
        switch (code) {
            case USER_NOT_EXIST:
                message = "用户名不存在";
                break;
            case WRONG_PASSWORD:
                message = "密码错误";
                break;
            case errData:
                message = "服务端返回数据格式不正确";
                break;
            default:
                message = "未知错误";
        }
        return message;
    }
}

