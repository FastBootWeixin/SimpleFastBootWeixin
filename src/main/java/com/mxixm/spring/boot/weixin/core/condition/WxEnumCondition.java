package com.mxixm.spring.boot.weixin.core.condition;

import com.mxixm.spring.boot.weixin.core.WxRequestBody;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

// 枚举类型条件
public class WxEnumCondition<T extends Enum<T>> implements WxRequestCondition<WxEnumCondition> {
    // 保存条件枚举集合
    private Collection<T> contents;
    // 从微信请求体获取需要匹配枚举的方法
    private Function<WxRequestBody, T> fun;
    // 传入如何从请求获取要匹配内容的方法和注解中定义的条件属性数组构造条件
    public WxEnumCondition(Function<WxRequestBody, T> fun, T... contents) {
        this.fun = fun;
        this.contents = Arrays.asList(contents);
    }
    // 是否匹配，如果内容为空则直接匹配，否则判断枚举内容是否包含当前请求体中的属性值，包含则匹配
    @Override
    public WxEnumCondition getMatchingCondition(WxRequestBody body) {
        if (contents.isEmpty()) {
            return this;
        }
        return contents.contains(fun.apply(body)) ? new WxEnumCondition(fun, fun.apply(body)) : null;
    }

    // 匹配结果均相同
    @Override
    public int compareTo(WxEnumCondition other, WxRequestBody body) {
        return 0;
    }
    // 返回内容集合
    public Collection<T> getContents() {
        return contents;
    }
}
