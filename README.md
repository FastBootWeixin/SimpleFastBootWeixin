# How To Use

### 1. 申请测试公众号

[微信测试公众号申请链接](https://mp.weixin.qq.com/debug/cgi-bin/sandbox?t=sandbox/login)

### 2. 配置内网穿透

因为微信公众号需要配置自己的服务器接口，测试时可直接使用本地进行测试，使用内网穿透可以令微信公众平台访问到你自己本地的服务器。

软件可使用[ngrok](https://www.ngrok.cc/)或者[natapp](https://natapp.cn/)，使用方式请参考两者官方文档。

启动后生成的域名url地址可以配置在wx.callback-url中，以便进行oauth2认证。授权回调页面域名中也需配置上上面生成的url中的域名。

### 3. 配置测试公众号

在测试公众号的接口配置信息中填写在第四步中生成的域名，token使用配置文件中的token，保存后，如果不出意外应该会验证成功。如有问题请及时反馈。
