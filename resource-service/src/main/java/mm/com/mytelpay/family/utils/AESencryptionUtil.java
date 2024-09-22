/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AESencryptionUtil {
    
        public static Logger logger = LogManager.getLogger(Util.class);
        
        @Value("${app.aes.secretKey}")
        private String aesSecretKey;

        public static String AES_SECRET_KEY;

        @PostConstruct
        public void init() {
            AES_SECRET_KEY = aesSecretKey;
        }
        public static <T> T decryptAES(String encodeString, Class<T> clazz) {

        try {
            SecretKey key = new SecretKeySpec(AES_SECRET_KEY.getBytes(), "AES");
            Cipher cipher;
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encodeString));
            String jsonData = new String(decryptedBytes);
            return ObjectMapperUtil.convertJsonToObject(jsonData, clazz);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            return null;
        }
    }

    public static <T> String encryptAES(T resource) {
        try {
            logger.info("reource: "+resource);
            String raw = ObjectMapperUtil.toJsonString(resource);
            logger.info("raw: "+raw);
            logger.info("secret: " + AES_SECRET_KEY);
            SecretKey key = new SecretKeySpec(AES_SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(raw.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            logger.error("encrypt AES Error: " + ex);
            return "";
        }
    }
}
