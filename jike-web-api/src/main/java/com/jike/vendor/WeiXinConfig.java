package com.jike.vendor;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 初始化配置信息
 */
@Component
@Data
public class WeiXinConfig {

    private String officeAppId;
    private String officeSecret;
    private String officeToken;
    private String officeAesKey;

    private String mchId;
    private String privateKeyPath;
    private String mchSerialNo;
    private String apiV3Key;
    private String notifyUrl = "https://api-v2.jikeing.com/wechat/notify/pay-result";

    @Value("${app.env}")
    private String env;

    @PostConstruct
    private void configInfo() {
        if (env.contains("test")) {
            this.notifyUrl = "https://api-v2-test.jikeing.com/wechat/notify/pay-result";
        }
        if (env.contains("prod") || env.contains("test")) {
            this.officeAppId = System.getenv("officeAppId");
            this.officeSecret = System.getenv("officeSecret");
            this.officeToken = System.getenv("officeToken");
            this.officeAesKey = System.getenv("officeAesKey");

            this.mchId = System.getenv("mchId");
            this.privateKeyPath = System.getenv("privateKeyPath");
            this.mchSerialNo = System.getenv("mchSerialNo");
            this.apiV3Key = System.getenv("apiV3Key");

        } else {
            this.notifyUrl = "https://api.jikeing.com/v1/wechat/pay-notify";

            this.officeAppId = "wxfb3fa270b1e2ae9e";
            this.officeSecret = "5b2698a5b148d8addc5a4713c96d28f2";  // TODO: 请确认是否需要修改
            this.officeToken = "jike666";
            this.officeAesKey = "7xUZM43ZP5OSJ17FrrSDWyUVfRy7r72xUfOAkI9UrGY";

            this.mchId = "1110510960";
            this.privateKeyPath = "d:\\code\\yunyun\\积分\\java\\jike-web-api\\wxpay\\apiclient_key.pem";
            this.mchSerialNo = "471C385E81067C9B7ACD2F32DA07229E2DB97571";
            this.apiV3Key = "7a2f9d4c1e8b3c6k5s9v2x7r4t1y6u8p";

        }
    }

}