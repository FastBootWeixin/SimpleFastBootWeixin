package com.mxixm.spring.boot.weixin.core.condition;

import com.mxixm.spring.boot.weixin.core.WxRequestBody;

public interface WxRequestCondition<T> {

    // 获取匹配条件
    T getMatchingCondition(WxRequestBody body);

    // 两个匹配结果比较
    int compareTo(T other, WxRequestBody body);

}
