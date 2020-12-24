package com.mxixm.spring.boot.weixin.core.condition;

import com.mxixm.spring.boot.weixin.core.WxRequestBody;

import java.util.Arrays;
import java.util.Collection;

// 事件Key条件
public class WxEventKeyCondition implements WxRequestCondition<WxEventKeyCondition> {
    // 保存条件集合
    private Collection<String> contents;
    // 使用eventKey条件数组创建条件
    public WxEventKeyCondition(String... contents) {
        this.contents = Arrays.asList(contents);
    }
    // 是否匹配，如果内容为空则直接匹配，否则判断条件集合中是否包含当前请求体中的eventKey，包含则匹配
    @Override
    public WxEventKeyCondition getMatchingCondition(WxRequestBody body) {
        if (contents.isEmpty()) {
            return this;
        }
        return this.contents.contains(body.getEventKey()) ? new WxEventKeyCondition(body.getEventKey()) : null;
    }
    // 匹配结果均相同
    @Override
    public int compareTo(WxEventKeyCondition other, WxRequestBody body) {
        return 0;
    }
}
