package com.mxixm.spring.boot.weixin.core;

import javax.servlet.http.HttpServletRequest;

public class WxRequestUtils {

    public static final String WX_REQUEST_BODY = "WX.RequestBody";

    public static void setWxRequestBody(HttpServletRequest request, WxRequestBody body) {
        request.setAttribute(WX_REQUEST_BODY, body);
    }

    public static WxRequestBody getWxRequestBody(HttpServletRequest request) {
        return (WxRequestBody) request.getAttribute(WX_REQUEST_BODY);
    }

}
