/*
 * Copyright (c) 2016-2017, Guangshan (guangshan1992@qq.com) and the original author or authors.
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

import com.mxixm.spring.boot.weixin.core.WxRequestBody;
import com.mxixm.spring.boot.weixin.core.WxRequestUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

// 标记为处理器增强器
@ControllerAdvice
public class WxMessageResponseBodyAdvice implements ResponseBodyAdvice<WxMessage> {

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        // 只支持XML格式，且返回值类型是WxMessage或者其子类
        return AbstractXmlHttpMessageConverter.class.isAssignableFrom(converterType) &&
                WxMessage.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public WxMessage beforeBodyWrite(WxMessage body, MethodParameter returnType,
                                     MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                     ServerHttpRequest request, ServerHttpResponse response) {
        // 确定request类型以及body不为null才进行处理
        if (!(request instanceof ServletServerHttpRequest) || body == null) {
            return body;
        }
        // 获取原始请求类型
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        // 获取请求属性中保存的事件请求体
        WxRequestBody wxRequestBody = WxRequestUtils.getWxRequestBody(servletRequest);
        // 设置请求体的to为返回消息体的from
        body.setFromUser(wxRequestBody.getToUserName());
        // 设置请求体的from为返回消息体的to
        body.setToUser(wxRequestBody.getFromUserName());
        // 返回处理后的消息体
        return body;
    }

}
