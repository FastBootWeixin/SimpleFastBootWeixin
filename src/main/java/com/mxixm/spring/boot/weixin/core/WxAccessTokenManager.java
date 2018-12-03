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

package com.mxixm.spring.boot.weixin.core;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;

// 标记为Spring的Bean组件
@Component
public class WxAccessTokenManager implements InitializingBean {
    // 注入配置的appId值
    @Value("${wx.appId}")
    private String appId;
    // 注入配置的appSecret值
    @Value("${wx.appSecret}")
    private String appSecret;
    // 用于发起Http请求的RestTemplate
    private RestTemplate restTemplate;
    // 上一次刷新时间
    private long lastRefreshTime;
    // 保存的AccessToken
    private String accessToken;
    // 初始化方法
    @Override
    public void afterPropertiesSet() throws Exception {
        restTemplate = new RestTemplate();
        // 启动时先初始化一次AccessToken提高后续访问速度
        this.get();
    }
    // 获取AccessToken方法
    public String get() {
        long now = Instant.now().toEpochMilli();
        // 如果当前已过期，默认一次获取7200000毫秒后过期
        if (now > lastRefreshTime + 7200000) {
            // 从接口获取AccessToken，更新当前内存维护的AccessToken
            accessToken = this.fetchAccessToken();
            // 更新上次刷新时间
            lastRefreshTime = now;
        }
        // 返回AccessToken，未过期直接返回
        return accessToken;
    }
    // 从接口获取AccessToken值
    public String fetchAccessToken() {
        // 构造请求地址
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com/cgi-bin/token")
                .queryParam("grant_type", "client_credential")
                .queryParam("appid", appId)
                .queryParam("secret", appSecret);
        // 调用接口获取AccessToken响应结果
        String result = restTemplate.getForObject(builder.toUriString(), String.class);
        // 从结果中提取AccessToken
        return extractAccessToken(result);
    }

    private static final String startString = "\"access_token\":\"";

    private static final String endString = "\",";

    private String extractAccessToken(String result) {
        int startIndex = result.indexOf("\"access_token\":\"");
        int endIndex = result.indexOf(endString, startIndex);
        return result.substring(startIndex + startString.length(), endIndex);
    }

}
