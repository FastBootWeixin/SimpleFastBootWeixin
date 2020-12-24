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

package com.mxixm.spring.boot.weixin.core.annotation;

import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

// 消息事件映射
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ResponseBody
public @interface WxMessageMapping {

    /**
     * 映射信息名
     */
    String name() default "";

    /**
     * 匹配消息类型
     */
    Type[] type() default {};

    /**
     * 匹配消息内容
     */
    String[] contents() default {};

    enum Type {
        /**
         * 表示按钮事件或者系统事件
         */
        EVENT,

        /**
         * 文本消息
         */
        TEXT,


        /**
         * 图片消息
         */
        IMAGE,

        /**
         * 语音消息
         */
        VOICE,

        /**
         * 视频消息
         */
        VIDEO,

        /**
         * 小视频消息
         */
        SHORT_VIDEO,

        /**
         * 地理位置消息
         */
        LOCATION,

        /**
         * 链接消息
         */
        LINK,

        /**
         * 音乐消息
         */
        MUSIC,

        /**
         * 图文消息
         */
        NEWS,

    }

}
