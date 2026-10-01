package com.niujl.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niujl.common.Result;
import com.niujl.common.ResultCode;
import com.niujl.context.UserContext;
import com.niujl.util.JwtUtil;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * JWT 鉴权拦截器：解析 Authorization: Bearer &lt;jwt&gt;，将 userId 写入 {@link UserContext}。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader(HEADER);
        if (header == null || !header.startsWith(PREFIX)) {
            writeUnauthorized(response, ResultCode.UNAUTHORIZED);
            return false;
        }
        String token = header.substring(PREFIX.length()).trim();
        if (token.isEmpty()) {
            writeUnauthorized(response, ResultCode.UNAUTHORIZED);
            return false;
        }
        try {
            UserContext.setUserId(jwtUtil.parseUserId(token));
            return true;
        } catch (Exception e) {
            writeUnauthorized(response, ResultCode.TOKEN_INVALID);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response, ResultCode resultCode) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(resultCode)));
    }
}
