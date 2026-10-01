package com.niujl.util;

import com.niujl.common.BusinessException;
import com.niujl.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 敏感信息加解密工具（确定性 AES）。
 *
 * <p>固定 Key + 固定 IV + pepper 前缀，保证同一明文得到同一密文，
 * 因此密文可直接用于等值查询与唯一索引。</p>
 */
@Component
public class CryptoUtil {

    private static final Logger log = LoggerFactory.getLogger(CryptoUtil.class);

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String ALGORITHM = "AES";

    private final SecretKeySpec keySpec;
    private final IvParameterSpec ivSpec;
    private final String pepper;

    public CryptoUtil(@Value("${app.crypto.aes-key}") String aesKey,
                      @Value("${app.crypto.aes-iv}") String aesIv,
                      @Value("${app.crypto.pepper:}") String pepper) {
        this.keySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        this.ivSpec = new IvParameterSpec(aesIv.getBytes(StandardCharsets.UTF_8));
        this.pepper = pepper;
    }

    /**
     * 加密明文（自动拼接 pepper），返回 Base64 密文。
     */
    public String encrypt(String plain) {
        if (plain == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal((pepper + plain).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("敏感信息加密失败", e);
            throw new BusinessException(ResultCode.SERVER_ERROR);
        }
    }

    /**
     * 解密密文并去除 pepper；解密失败返回 null。
     */
    public String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            String plain = new String(decrypted, StandardCharsets.UTF_8);
            return plain.startsWith(pepper) ? plain.substring(pepper.length()) : plain;
        } catch (Exception e) {
            log.error("敏感信息解密失败");
            return null;
        }
    }

    /**
     * 解密后对手机号脱敏，如 138****8000。
     */
    public String maskPhone(String cipherText) {
        String plain = decrypt(cipherText);
        if (plain == null || plain.length() < 7) {
            return plain;
        }
        return plain.substring(0, 3) + "****" + plain.substring(plain.length() - 4);
    }
}
