package com.finflow.api_gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.file.AccessDeniedException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper){
        this.objectMapper=objectMapper;
    }

    @Override
    public Mono<Void>handle(ServerWebExchange exchange,Throwable ex){
        HttpStatus status=resolveStatus(ex);

        ServerHttpResponse response=exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String,Object>body=new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("path", exchange.getRequest().getPath().value());

        try{
            byte[]bytes=objectMapper.writeValueAsBytes(body);
            DataBuffer buffer=response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        }
        catch(Exception e){
            return response.setComplete();
        }
    }

    private HttpStatus resolveStatus(Throwable ex){
        if(ex instanceof ResponseStatusException responseStatusException){
            return HttpStatus.valueOf(responseStatusException.getStatusCode().value());
        }
        if(ex instanceof AccessDeniedException){
            return HttpStatus.FORBIDDEN;
        }
        if(ex instanceof JwtException){
            return HttpStatus.UNAUTHORIZED;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
