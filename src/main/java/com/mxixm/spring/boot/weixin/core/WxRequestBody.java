package com.mxixm.spring.boot.weixin.core;

import com.mxixm.spring.boot.weixin.core.annotation.WxButton;
import com.mxixm.spring.boot.weixin.core.annotation.WxEventMapping;
import com.mxixm.spring.boot.weixin.core.annotation.WxMessageMapping;

import java.util.Arrays;
import java.util.Date;

public class WxRequestBody {
    // 具体事件请求体类型，按钮事件、消息事件、系统事件
    enum Type {
        BUTTON, MESSAGE, EVENT
    }

    // 开发者微信号
    private String toUserName;

    // 发送方帐号（一个OpenID）
    private String fromUserName;

    // 消息创建时间 （整型）
    private Date createTime;

    // 消息类型
    private WxMessageMapping.Type messageType;

    // 事件请求体类型
    private Type type;

    // 事件类型
    private WxEventMapping.Type eventType;

    // 按钮类型
    private WxButton.Type buttonType;

    // 对事件请求体的eventKey
    private String eventKey;

    // 文本消息内容
    private String content;

    // 图片链接（由系统生成）
    private String picUrl;

    // image、voice、video类型的消息有，消息媒体id，可以调用多媒体文件下载接口拉取数据。
    private String mediaId;

    // 语音格式，如amr，speex等
    private String format;

    // voice类型语音识别结果，UTF8编码
    private String recognition;

    // 视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据。
    private String thumbMediaId;

    // location消息地理位置维度
    private Double locationX;

    // location消息地理位置经度
    private Double locationY;

    // location消息地图缩放大小
    private Integer scale;

    // location消息地理位置信息
    private String label;

    // link消息标题
    private String title;

    // link消息描述
    private String description;

    // link消息链接
    private String url;

    // 消息id，64位整型
    private Long msgId;

    // button事件的类型
    public WxButton.Type getButtonType() {
        // 已经获取过，直接返回
        if (this.buttonType != null) {
            return this.buttonType;
        }
        // 只有msgType是event时才是buttonType
        if (this.messageType == WxMessageMapping.Type.EVENT) {
            // 如果当前事件请求体的eventType属性值在WxButton.Type枚举中，则返回WxButton.Type中该eventType的枚举，否则返回null
            this.buttonType = Arrays.stream(WxButton.Type.values())
                    .filter(t -> t.name().equals(this.eventType.name()))
                    .findFirst().orElse(null);
        }
        return this.buttonType;
    }

    // 事件请求体类型获取逻辑
    public Type getType() {
        // 已经获取过，则直接返回
        if (type != null) {
            return type;
        }
        if (this.messageType == WxMessageMapping.Type.EVENT) {
            // 有button类型，则是button
            if (this.getButtonType() != null) {
                type = Type.BUTTON;
            } else {
                // 否则是事件
                type = Type.EVENT;
            }
        } else {
            // 否则就是消息
            type = Type.MESSAGE;
        }
        return type;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public WxMessageMapping.Type getMessageType() {
        return messageType;
    }

    public void setMessageType(WxMessageMapping.Type messageType) {
        this.messageType = messageType;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public WxEventMapping.Type getEventType() {
        return eventType;
    }

    public void setEventType(WxEventMapping.Type eventType) {
        this.eventType = eventType;
    }

    public void setButtonType(WxButton.Type buttonType) {
        this.buttonType = buttonType;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getRecognition() {
        return recognition;
    }

    public void setRecognition(String recognition) {
        this.recognition = recognition;
    }

    public String getThumbMediaId() {
        return thumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId) {
        this.thumbMediaId = thumbMediaId;
    }

    public Double getLocationX() {
        return locationX;
    }

    public void setLocationX(Double locationX) {
        this.locationX = locationX;
    }

    public Double getLocationY() {
        return locationY;
    }

    public void setLocationY(Double locationY) {
        this.locationY = locationY;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }
}
