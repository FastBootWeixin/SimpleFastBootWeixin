package com.mxixm.spring.boot.weixin.core.condition;

import com.mxixm.spring.boot.weixin.core.WxRequestBody;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

// 消息内容条件
public class WxContentCondition implements WxRequestCondition<WxContentCondition> {
    // 保存内容条件集合
    private Collection<String> contents;
    public WxContentCondition(String... contents) {
        this.contents = Arrays.asList(contents);
    }
    // 是否匹配，如果内容为空则直接匹配，否则判断消息内容是否包含内容条件集合中的值，返回包含的全部条件属性。如果不包含则返回null
    @Override
    public WxContentCondition getMatchingCondition(WxRequestBody body) {
        if (contents.isEmpty()) {
            return this;
        }
        if (body.getContent() == null) {
            return null;
        }
        String[] result = this.contents.stream().filter(c -> body.getContent().contains(c)).toArray(String[]::new);
        return result.length == 0 ? null : new WxContentCondition(result);
    }
    // 找到匹配结果中最长匹配内容，再看最长匹配内容长度，越长视为匹配度越高
    @Override
    public int compareTo(WxContentCondition other, WxRequestBody body) {
        return other.contents.stream().map(String::length).max(Integer::compareTo).orElse(0)
                - this.contents.stream().map(String::length).max(Integer::compareTo).orElse(0);
    }
}
