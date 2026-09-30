package com.niujl.service.sms;

/**
 * 短信发送抽象。
 *
 * <p>实现按配置自动选择：{@code app.sms.mock=true} 时使用 {@link MockSmsSender}，
 * 否则使用真实实现；后续填好服务商凭证并把 {@code app.sms.mock} 置为 false 即可切换。</p>
 */
public interface SmsSender {

    /**
     * 发送验证码短信。
     *
     * @param phone 明文手机号（用于投递，禁止落库）
     * @param code  验证码
     */
    void send(String phone, String code);
}
