package com.yzw.sms.demo.test;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author YaoZuoWei
 * @date 2021/1/7 14:48
 * @Description:
 */
@SpringBootTest
public class JasyptTest {


    @Test
    public void testEncrypt() throws Exception {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();

        config.setAlgorithm("PBEWithMD5AndDES");          // 加密的算法，这个算法是默认的
        config.setPassword("yaozuowei");                        // 加密的密钥，随便自己填写，很重要千万不要告诉别人
        standardPBEStringEncryptor.setConfig(config);
        String plainText = "WH4AdFzpEEEqKQg49v8XTeUihv4F0U";         //自己的密码
        String encryptedText = standardPBEStringEncryptor.encrypt(plainText);
        System.out.println(encryptedText);
    }

    @Test
    public void testDe() throws Exception {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();

        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPassword("yaozuowei");
        standardPBEStringEncryptor.setConfig(config);
        String encryptedText = "/Qwe1EBzhlI31tW81PN6uEGzD+xRC/SxQnVdOIKIcZBd+On9dLjucQ==";   //加密后的密码
        String plainText = standardPBEStringEncryptor.decrypt(encryptedText);
        System.out.println(plainText);
    }
}
