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

package com.mxixm.spring.boot.weixin.core;

import com.mxixm.spring.boot.weixin.core.annotation.WxButton;
import com.mxixm.spring.boot.weixin.core.annotation.WxEventMapping;
import com.mxixm.spring.boot.weixin.core.annotation.WxMessageMapping;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;

public class WxArgumentResolver implements HandlerMethodArgumentResolver {
    // 是否支持解析参数
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 只有method上有注解微信相关注解时才支持解析
        return AnnotatedElementUtils.hasAnnotation(parameter.getMethod(), WxButton.class) ||
                AnnotatedElementUtils.hasAnnotation(parameter.getMethod(), WxMessageMapping.class) ||
                AnnotatedElementUtils.hasAnnotation(parameter.getMethod(), WxEventMapping.class);
    }
    // 解析参数值方法
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        // 获取request中的WxRequestBody数据
        WxRequestBody body = WxRequestUtils.getWxRequestBody(servletRequest);
        // 类型匹配，直接返回WxRequestBody
        if (parameter.getParameterType() == WxRequestBody.class) {
            return body;
        }
        // 否则获取事件请求体中参数名对应的属性值
        return getParameter(parameter.getParameterName(), body);
    }
    // 通过反射获取body中name对应的属性值
    private Object getParameter(String name, WxRequestBody body) {
        // 获取name属性描述符
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(WxRequestBody.class, name);
        if (propertyDescriptor == null) {
            return null;
        }
        try {
            // 调用属性描述符的getter方法获取返回值
            return propertyDescriptor.getReadMethod().invoke(body);
        } catch (Exception e) {
            // ignore it
        }
        return null;
    }
}
