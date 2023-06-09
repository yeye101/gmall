package com.yeye.mall.thirdparty.component;

import com.yeye.mall.thirdparty.util.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "sms.config")
@Component
@Data
public class SmsComponent {
  private Integer time;

  private String host;
  private String path;
  private String appcode;
  private String smsSignId;
  private String templateId;


  public void sendSmsCode(String phone, String code) {
    if (time == null) {
      sendSmsCode(phone, code, 10);
    }else {
      sendSmsCode(phone, code, time);
    }
  }

  public void sendSmsCode(String phone, String code, Integer time) {
    String method = "POST";
    Map<String, String> headers = new HashMap<String, String>();
    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
    headers.put("Authorization", "APPCODE " + appcode);
    Map<String, String> querys = new HashMap<String, String>();
    querys.put("mobile", phone);
    querys.put("param", "**code**:" + code + ",**minute**:" + time);
    querys.put("smsSignId", smsSignId);
    querys.put("templateId", templateId);
    Map<String, String> bodys = new HashMap<String, String>();


    try {
      HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
//      System.out.println(response.toString());
      //获取response的body
      System.out.println(EntityUtils.toString(response.getEntity()));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
