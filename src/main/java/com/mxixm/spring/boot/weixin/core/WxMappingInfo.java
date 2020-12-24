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

import com.mxixm.spring.boot.weixin.core.annotation.WxEventMapping;
import com.mxixm.spring.boot.weixin.core.annotation.WxMessageMapping;
import com.mxixm.spring.boot.weixin.core.condition.WxContentCondition;
import com.mxixm.spring.boot.weixin.core.condition.WxEnumCondition;
import com.mxixm.spring.boot.weixin.core.condition.WxEventKeyCondition;
import com.mxixm.spring.boot.weixin.core.condition.WxRequestCondition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.invoke.MethodHandles;

// 微信映射信息
public class WxMappingInfo implements WxRequestCondition<WxMappingInfo> {
    // 映射名
    private final String name;
    // 微信事件请求类型条件
    private final WxEnumCondition<WxRequestBody.Type> wxRequestTypes;
    // 微信事件请求消息类型条件
    private final WxEnumCondition<WxMessageMapping.Type> wxMessageTypes;
    // 微信事件请求系统事件类型条件
    private final WxEnumCondition<WxEventMapping.Type> wxEventTypes;
    // 微信事件key条件
    private final WxEventKeyCondition wxEventKey;
    // 文本消息内容条件
    private final WxContentCondition wxContent;
    // 根据注解信息构造映射信息
    public WxMappingInfo(String name,
                         WxEnumCondition<WxRequestBody.Type> wxRequestTypes,
                         WxEnumCondition<WxMessageMapping.Type> wxMessageTypes,
                         WxEnumCondition<WxEventMapping.Type> wxEventTypes,
                         WxEventKeyCondition wxEventKey,
                         WxContentCondition wxContent) {
        // 每个条件都不为null
        this.name = (name != null ? name : "");
        this.wxRequestTypes = wxRequestTypes != null ? wxRequestTypes : new WxEnumCondition<>(WxRequestBody::getType);
        this.wxMessageTypes = wxMessageTypes != null ? wxMessageTypes : new WxEnumCondition<>(WxRequestBody::getMessageType);
        this.wxEventTypes = wxEventTypes != null ? wxEventTypes : new WxEnumCondition<>(WxRequestBody::getEventType);
        this.wxEventKey = wxEventKey != null ? wxEventKey : new WxEventKeyCondition();
        this.wxContent = wxContent != null ? wxContent : new WxContentCondition();
    }
    // 根据事件请求体获取匹配结果
    @Override
    public WxMappingInfo getMatchingCondition(WxRequestBody body) {
        WxEnumCondition wxRequestTypes = this.wxRequestTypes.getMatchingCondition(body);
        WxEnumCondition wxMessageTypes = this.wxMessageTypes.getMatchingCondition(body);
        WxEnumCondition wxEventTypes = this.wxEventTypes.getMatchingCondition(body);
        WxEventKeyCondition wxEventKey = this.wxEventKey.getMatchingCondition(body);
        WxContentCondition wxContent = this.wxContent.getMatchingCondition(body);
        // 任何一个不匹配均不返回
        if (wxRequestTypes == null || wxMessageTypes == null || wxEventTypes == null || wxEventKey == null || wxContent == null ) {
            return null;
        }
        // 返回匹配结果
        return new WxMappingInfo(name, wxRequestTypes, wxMessageTypes, wxEventTypes, wxEventKey, wxContent);
    }

    // 两个匹配结果比较，只根据匹配内容比较
    @Override
    public int compareTo(WxMappingInfo other, WxRequestBody body) {
        return this.wxContent.compareTo(other.wxContent, body);
    }

    public WxEnumCondition<WxRequestBody.Type> getWxRequestTypes() {
        return wxRequestTypes;
    }

    public WxEnumCondition<WxMessageMapping.Type> getWxMessageTypes() {
        return wxMessageTypes;
    }

    public WxEnumCondition<WxEventMapping.Type> getWxEventTypes() {
        return wxEventTypes;
    }

    public WxEventKeyCondition getWxEventKey() {
        return wxEventKey;
    }

    public WxContentCondition getWxContent() {
        return wxContent;
    }
}
