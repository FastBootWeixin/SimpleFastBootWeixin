package com.mxixm.spring.boot.weixin.core;

import com.mxixm.spring.boot.weixin.util.CryptUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class WxTokenVerifyController {
    // 注入配置中的wx.token值
    @Value("${wx.token}")
    private String token;
    // 处理配置的wx.path路径下的get请求，因为token验证请求中包含四个参数，所以可以直接使用作为参数条件使用
    @GetMapping(path = "${wx.path}", params = {"signature", "timestamp", "nonce", "echostr"})
    // 作为相应体返回，如果不标记@ResponseBody，返回的字符串会被作为视图名处理。也可以直接标记控制器为@RestController
    @ResponseBody
    public String tokenVerify(@RequestParam("signature") String signature,
                              @RequestParam("timestamp") String timestamp,
                              @RequestParam("nonce") String nonce,
                              @RequestParam("echostr") String echostr) {
        // 三个参数值按字典排序并拼接
        String rawString = Stream.of(token, timestamp, nonce).sorted().collect(Collectors.joining());
        // 对拼接后字符串使用工具类中sha1方法进行sha1加密之后和参数中signature比较
        if (signature.equals(CryptUtils.encryptSHA1(rawString))) {
            // 相同则是合法的token认证请求，返回请求参数echostr值作为响应
            return echostr;
        }
        // 否则啥也不返回
        return null;
    }
}
