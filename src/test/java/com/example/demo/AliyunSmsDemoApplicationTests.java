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
 * @date 2020-08-13 16:20
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

    /**
     * Create a random verification code of specified length
     * @param flag true -> [0-9]    false -> [0-9][a-z]
     * @param length Length of verification code
     * @return Random verification code
     */
    public static String randomAuthCode(boolean flag, int length) {

        // Verification code
        StringBuilder authCode = new StringBuilder();
        // Authorized character set
        String authSet = flag ? "1234567890" : "1234567890abcdefghijklmnopqrstuvwxyz";
        // Number of authorized characters
        int len = authSet.length();

        while (length > 0) {
            // Randomly generate a subscript of an authorized character set
            // Math.random() -> [0.0, 1.0) double
            // Math.floor(double a) 向下取整
            int subscript = (int) Math.floor(Math.random() * len);

            // Verification code keywords
            char code = authSet.charAt(subscript);

            // verification code += verification code keywords
            authCode.append(code);

            length--;
        }

        return authCode.toString();
    }

    @Test
    void authTest() {
        // 6-digit, digital random verification code
        System.out.println("length: 6, type: digit");
        System.out.println(randomAuthCode(true, 6));

        // 6 digits, [numbers, alphabets] random verification code
        System.out.println("length: 6, type: digit、alphabet");
        System.out.println(randomAuthCode(false, 6));
    }

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

        // TemplateParam: {"code":"1111"}
        HashMap<String, String> paramMap = new HashMap<>();
        // auth code
        String authCode = randomAuthCode(true, 6);
        paramMap.put("code", authCode);
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
