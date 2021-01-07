package com.yzw.sms.demo.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author YaoZuoWei
 * @date 2021/1/6 15:47
 * @Description:
 */
@Data
@NoArgsConstructor //无参
@AllArgsConstructor //有参
public class SmsRedis {

    //电话号码
    private String phone;
    //短信验证码
    private String code;
    //短信验证码生成时间
    private Long time;


}
