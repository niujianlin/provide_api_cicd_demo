package com.niujl.context;

import com.niujl.common.BusinessException;
import com.niujl.common.ResultCode;

/**
 * 当前登录用户上下文（ThreadLocal）。
 * 由 {@code AuthInterceptor} 写入，请求结束后必须调用 {@link #clear()} 清理，避免线程复用污染。
 */
public final class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    /**
     * 获取当前登录用户 ID，未登录时抛出业务异常。
     */
    public static Long requireUserId() {
        Long userId = USER_ID.get();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }

    public static void clear() {
        USER_ID.remove();
    }
}
