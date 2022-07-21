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
import com.mxixm.spring.boot.weixin.core.annotation.WxController;
import com.mxixm.spring.boot.weixin.core.annotation.WxEventMapping;
import com.mxixm.spring.boot.weixin.core.annotation.WxMessageMapping;
import com.mxixm.spring.boot.weixin.core.condition.WxContentCondition;
import com.mxixm.spring.boot.weixin.core.condition.WxEnumCondition;
import com.mxixm.spring.boot.weixin.core.condition.WxEventKeyCondition;
import com.mxixm.spring.boot.weixin.core.menu.WxMenu;
import com.mxixm.spring.boot.weixin.core.menu.WxMenuManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

// 标记为组件，顺序放在最前面，以有限支持微信事件请求
@Component
public class WxMappingHandlerMapping extends AbstractHandlerMethodMapping<WxMappingInfo> {
    // 注入wx.path属性，只有这个路径的POST请求，才视为微信事件请求
    @Value("wx.path")
    private String path;
    // 注入菜单管理器，用于在扫描到@WxButton注解时调用其addButton方法添加自定义菜单
    @Autowired
    private WxMenuManager wxMenuManager;
    // Xml请求体转换器，直接使用转换XML消息为实体的HttpMessageConverter
    private Jaxb2RootElementHttpMessageConverter wxXmlRequestBodyConverter = new Jaxb2RootElementHttpMessageConverter();

    // 重写getHandlerInternal方法，用于获取此事件请求对应的事件处理器方法，用于在事件请求处理时，先解析事件请求的请求体
    @Override
    protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
        // 获取请求的路径
        String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
        // 如果请求路径不是配置的wx.path，或请求方法不是POST，则不需要被微信事件请求映射处理，直接返回null
        if (!path.equals(lookupPath) || !"POST".equalsIgnoreCase(request.getMethod())) {
            return null;
        }
        // 通过消息转换器读取请求体为WxRequestBody类型实例
        WxRequestBody body = (WxRequestBody) wxXmlRequestBodyConverter.read(WxRequestBody.class, new ServletServerHttpRequest(request));
        // 放入请求属性中，用于后续处理使用，内部调用为request.setAttribute(WX_REQUEST_BODY, body)
        WxRequestUtils.setWxRequestBody(request, body);
        // 前置判断以及请求体解析执行完成，交给父类的获取处理器方法逻辑执行查找匹配逻辑
        // 父类遍历全部事件映射信息，找到与事件请求体属性匹配的事件处理器
        return super.getHandlerInternal(request);
    }

    // 重写isHandler判断逻辑，只有标记有@WxController注解的，才视为处理器Bean，查找内部全部方法
    @Override
    protected boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, WxController.class);
    }

    // 用于父类注册直接URL映射时获取映射信息的URL，这里使用映射信息里的WxRequestBody.Type类型枚举名作为url，以加快匹配速度
    @Override
    protected Set<String> getMappingPathPatterns(WxMappingInfo info) {
        return info.getWxRequestTypes().getContents().stream().map(Enum::name).collect(Collectors.toSet());
    }
    // 与上面方法配合使用，通过直接URL映射查找时，把当前事件请求体中请求类型名称作为直接URL查找映射信息
    @Override
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        return super.lookupHandlerMethod(WxRequestUtils.getWxRequestBody(request).getType().name(), request);
    }
    // 当匹配时
    @Override
    protected void handleMatch(WxMappingInfo mapping, String lookupPath, HttpServletRequest request) {
        super.handleMatch(mapping, lookupPath, request);
        // 再标记返回数据时需要返回XML
        if (mapping != null) {
            request.setAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, Collections.singleton(MediaType.TEXT_XML));
        }
    }
    // 通过映射信息根据请求获取映射匹配结果
    @Override
    protected WxMappingInfo getMatchingMapping(WxMappingInfo mapping, HttpServletRequest request) {
        return mapping.getMatchingCondition(WxRequestUtils.getWxRequestBody(request));
    }
    // 比较多个匹配结果
    @Override
    protected Comparator<WxMappingInfo> getMappingComparator(HttpServletRequest request) {
        return (info1, info2) -> info1.compareTo(info2, WxRequestUtils.getWxRequestBody(request));
    }
    // 根据方法创建映射信息
    @Override
    protected WxMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        // 优先创建按钮映射信息
        WxMappingInfo wxButtonInfo = createWxButtonInfo(method);
        // 不为null则返回该映射信息
        if (wxButtonInfo != null) {
            return wxButtonInfo;
        }
        // 再创建消息映射信息
        WxMappingInfo wxMessageMappingInfo = createWxMessageMappingInfo(method);
        // 不为null则返回
        if (wxMessageMappingInfo != null) {
            return wxMessageMappingInfo;
        }
        // 最后返回创建的系统事件映射信息
        return createWxEventMappingInfo(method);
        // 所以一个方法上标记多个注解时，只有一个可以生效，按上面顺序生效
    }
    // 创建按钮映射信息
    private WxMappingInfo createWxButtonInfo(AnnotatedElement element) {
        // 获取方法上标记的@WxButton注解
        WxButton wxButton = AnnotatedElementUtils.findMergedAnnotation(element, WxButton.class);
        // 没有则返回null
        if (wxButton == null) {
            return null;
        }
        // 调用菜单管理器创建按钮
        WxMenu.Button button = wxMenuManager.addButton(wxButton);
        // @WxButton按钮注解的标记的方法只能处理事件请求体类型为BUTTON的事件请求，所以该条件固定为BUTTON
        WxEnumCondition wxRequestTypes = new WxEnumCondition<>(WxRequestBody::getType, WxRequestBody.Type.BUTTON);
        // 按钮类型的事件请求，请求体中的消息类型固定为Event，所以此条件固定
        WxEnumCondition wxMessageTypes = new WxEnumCondition<>(WxRequestBody::getMessageType, WxMessageMapping.Type.EVENT);
        // 按钮事件Key的条件，类型为VIEW，则key为配置的URL，否则为创建按钮时自动生成的Key
        WxEventKeyCondition wxEventKey = new WxEventKeyCondition(wxButton.type() == WxButton.Type.VIEW ? button.getUrl() : button.getKey());
        // 创建映射信息，EventType不需要进行匹配，所以为null，content条件同上
        return new WxMappingInfo(wxButton.name(), wxRequestTypes, wxMessageTypes, null, wxEventKey, null);
    }
    // 创建消息映射信息
    private WxMappingInfo createWxMessageMappingInfo(AnnotatedElement element) {
        // 获取方法上的@WxMessageMapping注解信息
        WxMessageMapping wxMessageMapping = AnnotatedElementUtils.findMergedAnnotation(element, WxMessageMapping.class);
        // 没有则返回null
        if (wxMessageMapping == null) {
            return null;
        }
        // @WxMessageMapping注解标记的方法只能处理事件请求体类型为BUTTON的事件请求，所以该条件固定为BUTTON
        WxEnumCondition wxRequestTypes = new WxEnumCondition<>(WxRequestBody::getType, WxRequestBody.Type.MESSAGE);
        // 取注解的type属性作为消息类型条件
        WxEnumCondition wxMessageTypes = new WxEnumCondition<>(WxRequestBody::getMessageType, wxMessageMapping.type());
        // 文本消息类型的文本内容条件，使用注解中配置的属性作为条件列表
        WxContentCondition wxContent = new WxContentCondition(wxMessageMapping.contents());
        // 创建映射信息，系统事件类型和EventKey条件都为null
        return new WxMappingInfo(wxMessageMapping.name(), wxRequestTypes, wxMessageTypes, null, null, wxContent);
    }
    // 创建系统事件映射信息
    private WxMappingInfo createWxEventMappingInfo(AnnotatedElement element) {
        // 获取方法上的@WxEventMapping映射信息
        WxEventMapping wxEventMapping = AnnotatedElementUtils.findMergedAnnotation(element, WxEventMapping.class);
        // 没有则返回null
        if (wxEventMapping == null) {
            return null;
        }
        // @WxEventMapping注解标记的方法只能处理事件请求体类型为EVENT的事件请求，所以该条件固定为EVENT
        WxEnumCondition wxRequestTypes = new WxEnumCondition<>(WxRequestBody::getType, WxRequestBody.Type.EVENT);
        // 系统事件类型的事件请求，请求体中的消息类型固定为Event，所以此条件固定为EVENT
        WxEnumCondition wxMessageTypes = new WxEnumCondition<>(WxRequestBody::getMessageType, WxMessageMapping.Type.EVENT);
        // 系统事件类型直接的EventType判断条件，取注解中配置属性为条件
        WxEnumCondition wxEventTypes = new WxEnumCondition<>(WxRequestBody::getEventType, wxEventMapping.type());
        // 创建映射信息，EventKey条件为null，消息内容条件也为null
        return new WxMappingInfo(wxEventMapping.name(), wxRequestTypes, wxMessageTypes, wxEventTypes, null, null);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
    
}
