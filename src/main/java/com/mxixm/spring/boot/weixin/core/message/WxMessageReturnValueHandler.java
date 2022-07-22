/*
 * Copyright (c) 2016-2018, Guangshan (guangshan1992@qq.com) and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mxixm.spring.boot.weixin.core.message;

import com.mxixm.spring.boot.weixin.core.WxAccessTokenManager;
import com.mxixm.spring.boot.weixin.core.WxRequestBody;
import com.mxixm.spring.boot.weixin.core.WxRequestUtils;
import com.mxixm.spring.boot.weixin.core.annotation.WxEventMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

public class WxMessageReturnValueHandler implements HandlerMethodReturnValueHandler {
    // 发送请求
    private RestTemplate restTemplate = new RestTemplate();
    // 注入AccessToken管理器
    @Autowired
    private WxAccessTokenManager wxAccessTokenManager;
    // 是否支持该返回值
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        // 仅支持标记了@WxEventMapping注解的事件方法，且返回值类型需要是WxMessage或其子类
        return returnType.hasMethodAnnotation(WxEventMapping.class) &&
                WxMessage.class.isAssignableFrom(returnType.getParameterType());
    }
    // 处理返回值的方法
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 标记请求已被处理
        mavContainer.setRequestHandled(true);
        // 返回值转换为WxMessage类型
        WxMessage body = (WxMessage) returnValue;
        // 获取原始请求
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        // 获取请求属性中保存的事件请求体
        WxRequestBody wxRequestBody = WxRequestUtils.getWxRequestBody(servletRequest);
        // 设置请求体的to为返回消息体的from
        body.setFromUser(wxRequestBody.getToUserName());
        // 设置请求体的from为返回消息体的to
        body.setToUser(wxRequestBody.getFromUserName());
        // 构造包含access_token的客服消息发送接口URL
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com/cgi-bin/message/custom/send")
                .queryParam("access_token", wxAccessTokenManager.get());
        // 调用发送接口请求，把返回值作为消息体发送
        String result = restTemplate.postForObject(builder.toUriString(), returnValue, String.class);
        // 记录请求调用结果
        System.out.println(result);
    }

}
