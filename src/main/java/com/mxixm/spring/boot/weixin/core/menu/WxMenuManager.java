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

package com.mxixm.spring.boot.weixin.core.menu;

import com.mxixm.spring.boot.weixin.core.WxAccessTokenManager;
import com.mxixm.spring.boot.weixin.core.annotation.WxButton;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

// 微信菜单管理器，实现ApplicationListener<ApplicationReadyEvent>接口，用于监听应用准备就绪事件，在事件回调中执行创建自定义菜单逻辑
@Component
public class WxMenuManager implements ApplicationListener<ApplicationReadyEvent> {
    // 获取日志记录器
    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());
    // 用于保存所有一级菜单(主菜单)
    private Map<WxButton.Group, WxMenu.Button> mainButtonLookup = new HashMap<>();
    // 用于保存不同分组的二级菜单按钮列表，key是分组类型，value是菜单按钮列表。MultiValueMap的值都是列表类型。
    private MultiValueMap<WxButton.Group, WxMenu.Button> groupButtonLookup = new LinkedMultiValueMap<>();
    // 用于调用创建自定义菜单接口的RestTemplate
    private RestTemplate restTemplate = new RestTemplate();
    // 注入AccessToken管理器，用于调用接口
    @Autowired
    private WxAccessTokenManager wxAccessTokenManager;

    // 开放根据注解添加按钮功能，参数是@WxButton注解信息实例
    public WxMenu.Button addButton(WxButton wxButton) {
        // 根据注解信息构造菜单按钮实例
        WxMenu.Button button = new WxMenu.Button(wxButton);
        // 如果是一级菜单(主菜单)
        if (wxButton.main()) {
            // 确保一个分组最多只能有一个主菜单
            Assert.isNull(mainButtonLookup.get(button.getGroup()),
                    String.format("已经存在该分组的主菜单，分组是%s", button.getGroup()));
            // 添加到主菜单查找表中
            mainButtonLookup.put(button.getGroup(), button);
        } else {
            // 不是主菜单，则添加到分组对应列表中
            groupButtonLookup.add(button.getGroup(), button);
        }
        // 返回构造的button实例
        return button;
    }
    // 根据主菜单查找表和分组查找表获取WxMenu实例
    public WxMenu getMenu() {
        WxMenu wxMenu = new WxMenu();
        // 遍历主菜单查找表中数据，在遍历前先根据主菜单分组排序，顺序为LEFT、MIDDLE、RIGHT
        mainButtonLookup.entrySet().stream().sorted(Comparator.comparingInt(e -> e.getKey().ordinal()))
                .forEach(m -> {
                    // 遍历时，获取该分组下子菜单按钮列表，按照order排序
                    groupButtonLookup.getOrDefault(m.getKey(), new ArrayList<>()).stream()
                            .sorted(Comparator.comparingInt(w -> w.getOrder().ordinal()))
                            .forEach(b ->
                                    // 按顺序把子菜单按钮添加到当前主菜单的子菜单按钮列表中
                                    m.getValue().addSubButton(b));
                    // 把当前遍历中主菜单添加到菜单列表中
                    wxMenu.add(m.getValue());
                });
        // 返回构造好的WxMenu实例
        return wxMenu;
    }
    // 监听应用准备就绪事件，执行创建自定义菜单逻辑
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        // 获取WxMenu
        WxMenu wxMenu = this.getMenu();
        // 构造包含access_token的自定义菜单创建接口URL
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com/cgi-bin/menu/create")
                .queryParam("access_token", wxAccessTokenManager.get());
        // 调用创建自定义菜单接口，传入根据注解信息构造的自定义菜单实例
        String result = restTemplate.postForObject(builder.toUriString(),  wxMenu, String.class);
        // 打印结果
        logger.info("==============================================================");
        logger.info("            执行创建菜单操作");
        logger.info("            操作结果：" + result);
        logger.info("            新的菜单为：" + wxMenu);
        logger.info("==============================================================");
    }
}
