package com.mxixm.spring.boot.weixin.core.message;

import com.mxixm.spring.boot.weixin.core.annotation.WxMessageMapping;

import java.util.Date;

// 消息的通用属性类型
public class WxMessage {
    // 发送到的用户
    private String toUser;
    // 从哪个公众号发的
    private String fromUser;
    // 创建时间
    private Date createTime;
    // 消息类型
    private WxMessageMapping.Type msgType;

    public abstract static class Builder {
        protected String toUser;
        protected String fromUser;
        protected Date createTime;
        protected WxMessageMapping.Type msgType;

        public Builder toUser(String toUser) {
            this.toUser = toUser;
            return this;
        }

        public Builder fromUser(String fromUser) {
            this.fromUser = fromUser;
            return this;
        }

        public Builder createTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }

        public abstract WxMessage build();

        public String toString() {
            return "WxMessage.Builder(toUser=" + this.toUser + ", fromUser=" + this.fromUser + ", createTime=" + this.createTime + ", msgType=" + this.msgType + ")";
        }
    }


    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public WxMessageMapping.Type getMsgType() {
        return msgType;
    }

    public void setMsgType(WxMessageMapping.Type msgType) {
        this.msgType = msgType;
    }

}
