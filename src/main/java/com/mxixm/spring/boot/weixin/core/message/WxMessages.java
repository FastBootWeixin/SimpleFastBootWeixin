package com.mxixm.spring.boot.weixin.core.message;

public class WxMessages {

    public static WxTextMessage.Builder text() {
        return new WxTextMessage.Builder();
    }

    public static WxImageMessage.Builder image() {
        return new WxImageMessage.Builder();
    }

}
