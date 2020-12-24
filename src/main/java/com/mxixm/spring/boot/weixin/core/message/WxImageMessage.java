package com.mxixm.spring.boot.weixin.core.message;

import com.mxixm.spring.boot.weixin.core.annotation.WxMessageMapping;

import java.util.Date;

public class WxImageMessage extends WxMessage {

    // 图片消息体封装
    private Image image;

    public static class Image {
        // 图片消息体内容
        private String mediaId;
        // 构造方法
        public Image(String mediaId) {
            this.mediaId = mediaId;
        }

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

    }


    public static class Builder extends WxMessage.Builder {

        private String mediaId;

        public Builder mediaId(String mediaId) {
            this.mediaId = mediaId;
            return this;
        }

        public WxImageMessage build() {
            WxImageMessage message = new WxImageMessage();
            message.setImage(new Image(mediaId));
            message.setToUser(toUser);
            message.setFromUser(fromUser);
            message.setCreateTime(createTime != null ? createTime : new Date());
            message.setMsgType(WxMessageMapping.Type.IMAGE);
            return message;
        }

        public String toString() {
            return "WxImageMessage.Builder(mediaId=" + this.mediaId + ")";
        }
    }


    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
