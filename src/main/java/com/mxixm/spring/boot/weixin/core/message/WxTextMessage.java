package com.mxixm.spring.boot.weixin.core.message;

import com.mxixm.spring.boot.weixin.core.annotation.WxMessageMapping;

import java.util.Date;

public class WxTextMessage extends WxMessage {

    // 文本消息内容封装，由于消息的JSON结构是这样的，所以需要如此定义
    private Text text;

    public static class Text {
        // 具体的消息内容
        private String content;
        // 构造方法
        public Text(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class Builder extends WxMessage.Builder {
        private String content;

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public WxTextMessage build() {
            WxTextMessage message = new WxTextMessage();
            message.setToUser(toUser);
            message.setFromUser(fromUser);
            message.setCreateTime(createTime != null ? createTime : new Date());
            message.setMsgType(WxMessageMapping.Type.TEXT);
            message.setText(new Text(content));
            return message;
        }
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }
}
