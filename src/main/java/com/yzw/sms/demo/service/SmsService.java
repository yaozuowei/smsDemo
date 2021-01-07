package com.yzw.sms.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.yzw.sms.demo.exception.BaseBusinessException;
import com.yzw.sms.demo.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

/**
 * @author YaoZuoWei
 * @date 2021/1/6 15:22
 * @Description:
 */
@Service("SmsService")
@Slf4j
public class SmsService {
    @Autowired
    private SmsProperties smsProperties;

    @Autowired
    private RedisUtil redisUtil;


    /**
     * 根据用户输入的phone发送验证码
     * @param phone 电话号码
     */
    public SmsRedis sendSmsCode(String phone) throws BaseBusinessException {
        if(!phone.matches("^1[3|4|5|6|7|8][0-9]{9}$")){
            throw new BaseBusinessException(500,"手机号码格式不正确");
        }
        //判断用户输入的电话号码是否频繁发送
        if(isSendOfen(phone)){
            throw new BaseBusinessException(400,"发送短信频繁，请稍后再试");
        }

        //制作验证码，6位随机数字
        SmsRedis sms = makeCode(phone);
        JSONObject smsJson=new JSONObject();
        smsJson.put("code",sms.getCode());

        SendSmsResponse sendSmsResponse=null;
        try {
            sendSmsResponse = sendSms(phone,smsJson);
        } catch (ClientException e) {
            log.error("短信验证码发送失败");
            e.printStackTrace();
        }
        if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
            //短信发送成功，将短信记录到redis中
            redisCode(sms);
            log.info("短信发送成功");
        }

        return sms;
    }

    /**
     * 判断验证功发送时候频繁
     * @param phone
     * @return
     */
    private boolean isSendOfen(String phone) {
        if(redisUtil.get(phone)==null) {
            return false;
        }else{
            //判断上一次记录的时间和当前时间进行对比，如果两次相隔时间小于30s，视为短信发送频繁
            SmsRedis sms= (SmsRedis) redisUtil.get(phone);
            //两次发送短信中间至少有30秒的间隔时间
            if(sms.getTime()+30*1000>=System.currentTimeMillis()) {
                return true;
            }
            return false;
        }
    }

    /**
     * 将验证码缓存到redis中，5分钟过后自动清除该缓存
     * @param smsRedis
     */
    private void redisCode(SmsRedis smsRedis) {
        redisUtil.set(smsRedis.getPhone(),smsRedis,300);
    }

    /**
     * 随机生成6位数的短信码
     * @param phone
     * @return
     */
    private SmsRedis makeCode(String phone) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for(int i=0;i<6;i++){
            int next =random.nextInt(10);
            code.append(next);
        }
        return new SmsRedis(phone,code.toString(),System.currentTimeMillis());
    }

    /**
     * 验证短信
     * @param phone
     * @param code
     * @return
     */
    public boolean validSmsCode(String phone, String code) throws BaseBusinessException{
        //取出所有有关该手机号的短信验证码
        if(redisUtil.get(phone)==null){
            throw new BaseBusinessException(400,"短信验证失败");
        }
        SmsRedis sms= (SmsRedis) redisUtil.get(phone);
        if (sms.getCode().equals(code)){
            log.info("短信验证成功");
            //删除掉该redis
            redisUtil.remove(phone);
            return true;
        }
        return false;
    }


    /**
     * 发送时短信
     * @param phone
     * @param params
     * @return
     * @throws ClientException
     */
    SendSmsResponse sendSms(String phone,JSONObject params) throws ClientException {
        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsProperties.getAccessId(),
                smsProperties.getAccessKey());
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", smsProperties.getProduct(), smsProperties.getDomain());
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(smsProperties.getSignName());
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(smsProperties.getTemplateCode());
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam(params.toJSONString());
        request.setOutId(UUID.randomUUID().toString());
        //请求失败这里会抛ClientException异常
        return acsClient.getAcsResponse(request);
    }

}
