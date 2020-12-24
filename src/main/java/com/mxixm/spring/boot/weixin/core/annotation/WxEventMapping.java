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

import java.lang.annotation.*;

/**
 * 微信请求绑定
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WxEventMapping {

    /**
     * 映射信息名
     */
    String name() default "";

    /**
     * 请求事件的类型
     *
     * @return type
     */
    Type[] type() default {};

    /**
     * Event类型枚举
     */
    enum Type {

        /**
         * 订阅(关注)
         */
        SUBSCRIBE,
        /**
         * 取消订阅(关注)
         */
        UNSUBSCRIBE,
        // 由于所有的按钮类型也会通过Event属性传递，所以这里添加按钮类型的所有枚举
        CLICK, VIEW, SCANCODE_PUSH, SCANCODE_WAITMSG, PIC_SYSPHOTO, PIC_PHOTO_OR_ALBUM, PIC_WEIXIN, LOCATION_SELECT, MEDIA_ID, VIEW_LIMITED, MINI_PROGRAM
    }

}
