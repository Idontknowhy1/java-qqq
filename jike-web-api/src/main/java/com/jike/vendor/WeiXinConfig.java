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
            this.notifyUrl = "https://api-v2-test.jikeing.com/wechat/notify/pay-result";

            this.officeAppId = "wxd993834a5fc463e8";
            this.officeSecret = "5b2698a5b148d8addc5a4713c96d28f2";
            this.officeToken = "jike666";
            this.officeAesKey = "7xUZM43ZP5OSJ17FrrSDWyUVfRy7r72xUfOAkI9UrGY";

            this.mchId = "1676428333";
            this.privateKeyPath = "/Users/jjs/Documents/cers/jike/apiclient_key.pem";
            this.mchSerialNo = "6A6BC3FA8D75613F27DFC42FE459B5A8932A9304";
            this.apiV3Key = "MHHWHBXvQ3pyPP6H8rhk5EgdreGpfjyh";

        }
    }

}