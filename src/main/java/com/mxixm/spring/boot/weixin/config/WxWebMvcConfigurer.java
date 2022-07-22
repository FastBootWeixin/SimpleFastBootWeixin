package com.mxixm.spring.boot.weixin.config;

import com.mxixm.spring.boot.weixin.core.WxArgumentResolver;
import com.mxixm.spring.boot.weixin.core.message.WxMessageReturnValueHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

// 注册为配置类
@Configuration
public class WxWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 添加微信事件处理方法的参数解析器
        resolvers.add(new WxArgumentResolver());
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        // 添加返回值处理器
        handlers.add(wxMessageReturnValueHandler());
    }

    @Bean
    public WxMessageReturnValueHandler wxMessageReturnValueHandler() {
        return new WxMessageReturnValueHandler();
    }

}
