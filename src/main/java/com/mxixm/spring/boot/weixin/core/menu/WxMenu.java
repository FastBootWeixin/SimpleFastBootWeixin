package com.mxixm.spring.boot.weixin.core.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mxixm.spring.boot.weixin.core.annotation.WxButton;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * FastBootWeixin WxMenu
 * 微信一套菜单
 *
 * @author Guangshan
 * @date 2018/09/13 23:39
 * @since 0.7.0
 */
public class WxMenu {

    @JsonProperty("button")
    public List<Button> mainButtons = new ArrayList<>();

    public void add(Button button) {
        mainButtons.add(button);
    }

    public static class Button {

        @JsonProperty("sub_button")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<Button> subButtons = new ArrayList<>();

        @JsonIgnore
        private WxButton.Group group;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private WxButton.Type type;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String name;

        @JsonIgnore
        private boolean main;

        @JsonIgnore
        private WxButton.Order order;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String key;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String url;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("media_id")
        private String mediaId;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("appid")
        private String appId;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("pagepath")
        private String pagePath;

        public Button(WxButton wxButton) {
            this.group = wxButton.group();
            this.type = wxButton.type();
            this.main = wxButton.main();
            this.order = wxButton.order();
            this.name = wxButton.name();
            this.key = !StringUtils.isEmpty(wxButton.key()) ? wxButton.key() : "KEY_" + wxButton.group() + wxButton.order();
            this.url = wxButton.url();
            this.mediaId = wxButton.mediaId();
            this.appId = wxButton.appId();
            this.pagePath = wxButton.pagePath();
        }

        public Button addSubButton(Button item) {
            this.subButtons.add(item);
            return this;
        }

        public List<Button> getSubButtons() {
            return subButtons;
        }

        public void setSubButtons(List<Button> subButtons) {
            this.subButtons = subButtons;
        }

        public WxButton.Group getGroup() {
            return group;
        }

        public void setGroup(WxButton.Group group) {
            this.group = group;
        }

        public WxButton.Type getType() {
            return type;
        }

        public void setType(WxButton.Type type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isMain() {
            return main;
        }

        public void setMain(boolean main) {
            this.main = main;
        }

        public WxButton.Order getOrder() {
            return order;
        }

        public void setOrder(WxButton.Order order) {
            this.order = order;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getPagePath() {
            return pagePath;
        }

        public void setPagePath(String pagePath) {
            this.pagePath = pagePath;
        }
    }
}