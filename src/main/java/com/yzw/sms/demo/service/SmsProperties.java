package com.yzw.sms.demo.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author YaoZuoWei
 * @date 2021/1/6 15:24
 * @Description:
 */
@Data
@Component
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {
    private String accessId;
    private String accessKey;
    private String signName;
    private String templateCode;
    private String product;
    private String domain;
}
