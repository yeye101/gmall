package com.yeye.mall.thirdparty;

import com.aliyun.oss.OSSClient;
import com.yeye.mall.thirdparty.component.SmsComponent;
import com.yeye.mall.thirdparty.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallThirdPartyApplicationTests {

  @Autowired
  OSSClient ossClient;

  @Test
  public void testUpload() throws FileNotFoundException {

    //上传文件流。
    InputStream inputStream = new FileInputStream("--");
    ossClient.putObject("yeye-mall", "hahaha1.jpg", inputStream);

    // 关闭OSSClient。
    ossClient.shutdown();
    System.out.println("上传成功.");
  }

  @Autowired
  SmsComponent smsComponent;


  @Test
  public void testSms2() {
    smsComponent.sendSmsCode("15905874629","5566");
  }

  @Test
  public void testSms() {

    String host = "https://gyytz.market.alicloudapi.com";
    String path = "/sms/smsSend";
    String method = "POST";
    String appcode = "943503b0957141d9b981fc4d83a12ded";
    Map<String, String> headers = new HashMap<String, String>();
    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
    headers.put("Authorization", "APPCODE " + appcode);
    Map<String, String> querys = new HashMap<String, String>();
    querys.put("mobile", "15905874629");
    querys.put("param", "**code**:4499,**minute**:5");
    querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
    querys.put("templateId", "908e94ccf08b4476ba6c876d13f084ad");
    Map<String, String> bodys = new HashMap<String, String>();


    try {

      HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
      System.out.println(response.toString());
      //获取response的body
      //System.out.println(EntityUtils.toString(response.getEntity()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void contextLoads() {
  }

}
