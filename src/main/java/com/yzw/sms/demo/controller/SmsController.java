package com.yzw.sms.demo.controller;

import com.yzw.sms.demo.service.SmsRedis;
import com.yzw.sms.demo.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author YaoZuoWei
 * @date 2020/12/31 16:16
 * @Description:
 */
@RequestMapping("/sms")
@Controller
public class SmsController {
    @Resource(name = "SmsService")
    private SmsService smsService;

    private static final Logger LOG = LoggerFactory.getLogger(SmsController.class);

    /**
     * 短信发送
     *
     * @param phone
     * @return
     */
    @RequestMapping("/sendSms")
    @ResponseBody
    public SmsRedis ftp(@RequestParam("phone") String phone) {
        SmsRedis smsRedis=null;
        try {
            smsRedis=smsService.sendSmsCode(phone);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("/sendSms failure 结果：" + e.getMessage());
        }
        return smsRedis;
    }

}
