package com.example.demo;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

/**
 * @author doubleBeans
 * @since 2020-08-02 21:20
 * @version 0.0.1
 */
@SpringBootTest
class AliyunSmsDemoApplicationTests {

    // account and password for aliyun
    @Value("${accessKey.id}")
    private String accessKeyId;
    @Value("${accessKey.secret}")
    private String accessKeySecret;
    @Value("${queryParameter.phoneNumbers}")
    private String phoneNumbers;
    @Value("${queryParameter.signName}")
    private String signName;
    @Value("${queryParameter.templateCode}")
    private String templateCode;

    @Test
    void contextLoads() {

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");

        // set PhoneNumbers, SignName, TemplateCode, TemplateParam, etc...
        // you should apply for SignName and TemplateCode at Aliyun
        // user should give his or her PhoneNumbers to us
        // The TemplateParam is generated automatically by the program, it lasts about 5 min
        // so we should store it into redis later(for I don't know how to use redis now)
        // last but not least, TemplateParam should be a JSONString, we can use fastjson
        request.putQueryParameter("PhoneNumbers", phoneNumbers);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);

        // code should be randomly, now is a test
        // TemplateParam: {"code":"1111"}
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("code", 666888);
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(paramMap));

        // deal with exception
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

}
