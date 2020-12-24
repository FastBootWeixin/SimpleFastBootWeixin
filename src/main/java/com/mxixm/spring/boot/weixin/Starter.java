package com.mxixm.spring.boot.weixin;

import com.mxixm.spring.boot.weixin.core.WxRequestBody;
import com.mxixm.spring.boot.weixin.core.annotation.WxButton;
import com.mxixm.spring.boot.weixin.core.annotation.WxController;
import com.mxixm.spring.boot.weixin.core.annotation.WxEventMapping;
import com.mxixm.spring.boot.weixin.core.annotation.WxMessageMapping;
import com.mxixm.spring.boot.weixin.core.message.WxMessage;
import com.mxixm.spring.boot.weixin.core.message.WxMessages;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
// 标记为微信处理器
@WxController
public class Starter {
    // 启动入口，Java原生启动入口
    public static void main(String[] args) {
        SpringApplication.run(Starter.class, args);
    }

    // 关注时返回消息
    @WxEventMapping(type = WxEventMapping.Type.SUBSCRIBE)
    public WxMessage subscribe() {
        return WxMessages.text().content("欢迎关注测公众号").build();
    }

    // 左侧主菜单，有子菜单，故无处理逻辑
    @WxButton(group = WxButton.Group.LEFT, main = true, name = "左")
    public void left() {
    }
    // 中间菜单，返回一条文本消息
    @WxButton(group = WxButton.Group.MIDDLE, main = true, name = "中")
    public WxMessage middle() {
        return WxMessages.text().content("点击中间菜单").build();
    }
    // 右侧菜单，返回一条图片消息
    @WxButton(group = WxButton.Group.RIGHT, main = true, name = "右")
    public WxMessage right() {
        return WxMessages.image().mediaId("图片媒体ID").build();
    }
    // 左侧第一个菜单，点击返回消息
    @WxButton(type = WxButton.Type.CLICK,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.FIRST,
            name = "点击")
    public WxMessage left1() {
        return WxMessages.text().content("点击拉取消息").build();
    }

    // 左侧第二个菜单，点击跳转网页
    @WxButton(type = WxButton.Type.VIEW,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.SECOND,
            url = "https://github.com/FastBootWeixin",
            name = "跳转")
    public void left2() {
    }
    // 处理消息内容包含热点的消息事件，返回固定内容的文本消息
    @WxMessageMapping(type = WxMessageMapping.Type.TEXT, contents = "热点")
    public WxMessage textMessage() {
        return WxMessages.text().content("Spring Boot 2.0.2发布").build();
    }
    // 接收文本消息，原样返回该消息给发送者，包含热点的消息会被上一个处理器处理，不包含的都由此处理器处理
    @WxMessageMapping(type = WxMessageMapping.Type.TEXT)
    public WxMessage echoMessage(String content) {
        return WxMessages.text().content("收到消息内容为：" + content).build();
    }
    // 接收图片消息的处理器，返回图片地址
    @WxMessageMapping(type = WxMessageMapping.Type.IMAGE)
    public WxMessage imageMessage(WxRequestBody wxRequestBody) {
        return WxMessages.text().content("收到图片消息，图片地址为：" + wxRequestBody.getPicUrl()).build();
    }

}
