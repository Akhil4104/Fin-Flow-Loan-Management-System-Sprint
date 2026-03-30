package com.finflow.admin_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            
            String userId = request.getHeader("X-User-Id");
            if (userId != null) {
                template.header("X-User-Id", userId);
            }
            
            String userRole = request.getHeader("X-User-Role");
            if (userRole != null) {
                template.header("X-User-Role", userRole);
            }
            
            String userEmail = request.getHeader("X-User-Email");
            if (userEmail != null) {
                template.header("X-User-Email", userEmail);
            }

            String idempotencyKey = request.getHeader("Idempotency-Key");
            if (idempotencyKey != null) {
                template.header("Idempotency-Key", idempotencyKey);
            }
        }
    }
}
