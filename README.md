# aliyunSMS
## A demo about how to use aliyun short message service
敏感信息已进行加密（accessKeyId，accessKeySecret，etc...）  
参考文档：

+ [Jasypt](http://www.jasypt.org/)
+ [aliyun短信服务帮助文档](https://help.aliyun.com/product/44282.html?spm=5176.12207334.0.0.25571cbec1aduo)

实际项目需要自行实现**验证时间过期**  
+ 验证时间过期：集成redis，将PhoneNumbers和code存入redis中，并设置有效时间为5 min
