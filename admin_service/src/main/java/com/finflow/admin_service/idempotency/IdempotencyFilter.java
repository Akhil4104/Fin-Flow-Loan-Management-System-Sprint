package com.finflow.admin_service.idempotency;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class IdempotencyFilter extends OncePerRequestFilter {

    private static final String IDEMPOTENCY_HEADER = "Idempotency-Key";

    private final IdempotencyService idempotencyService;

    public IdempotencyFilter(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpMethod.POST.matches(request.getMethod())
                || !request.getRequestURI().matches("^/admin/applications/\\d+/decision$");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String key = request.getHeader(IDEMPOTENCY_HEADER);
        if (key == null || key.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<IdempotencyRecord> existing = idempotencyService.checkIfExists(key);
        if (existing.isPresent()) {
            if (existing.get().getStatusCode() != null) {
                writeStoredResponse(response, existing.get());
                return;
            }

            Optional<IdempotencyRecord> completed = idempotencyService.waitForCompletedRecord(key);
            if (completed.isPresent()) {
                writeStoredResponse(response, completed.get());
                return;
            }
        } else {
            IdempotencyRecord reserved = idempotencyService.reserveKey(key);
            if (reserved.getStatusCode() != null) {
                writeStoredResponse(response, reserved);
                return;
            }
        }

        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(request, cachingResponse);
            idempotencyService.saveResponse(
                    key,
                    new String(cachingResponse.getContentAsByteArray(), resolveCharset(cachingResponse)),
                    cachingResponse.getStatus());
        } finally {
            cachingResponse.copyBodyToResponse();
        }
    }

    private void writeStoredResponse(HttpServletResponse response, IdempotencyRecord record) throws IOException {
        response.setStatus(record.getStatusCode());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (record.getResponseBody() != null) {
            response.getWriter().write(record.getResponseBody());
        }
    }

    private Charset resolveCharset(ContentCachingResponseWrapper response) {
        String encoding = response.getCharacterEncoding();
        return encoding != null ? Charset.forName(encoding) : StandardCharsets.UTF_8;
    }
}
