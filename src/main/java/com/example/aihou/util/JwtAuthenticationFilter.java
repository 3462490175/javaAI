package com.example.aihou.util;

import com.example.aihou.common.ResultCode;
import com.example.aihou.config.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

// 不再用 @Component：改由 SecurityConfig 里的 @Bean 创建，
// 避免被 servlet 容器自动注册、和 Security 过滤链重复执行
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        boolean isPublic = SecurityConfig.isPublicPATH(request.getRequestURI());
        System.out.println("[JwtFilter] uri=" + request.getRequestURI() + ", isPublic=" + isPublic);
        return isPublic;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // 1. 提取 Token
        String token = jwtTokenUtil.extractTokenFromRequest(request);
        System.out.println("[JwtFilter] doFilterInternal, token=" + (StringUtils.hasText(token) ? "present" : "empty"));

        // 2. 没带 Token：401
        if (!StringUtils.hasText(token)) {
            SecurityContextHolder.clearContext();
            ResponseUtil.writeError(response, ResultCode.ACCESS_UNAUTHORIZED);
            return;
        }

        // 3. 验证 Token
        JwtTokenUtil.TokenVerificationResult validationResult = jwtTokenUtil.validateToken(token);
        System.out.println("[JwtFilter] validate result=" + (validationResult == null ? "null" : validationResult.isValid()));

        if (validationResult != null && validationResult.isValid()) {
            // 4. 验证通过，写入认证上下文
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            validationResult.getUsername(),
                            null,
                            Collections.emptyList()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("[JwtFilter] authenticated user=" + validationResult.getUsername());
            chain.doFilter(request, response);
        } else {
            // 5. Token 无效或过期：401
            SecurityContextHolder.clearContext();
            ResponseUtil.writeError(response, ResultCode.TOKEN_INVALID);
        }
    }
}
