package com.example.commerceplus.common.annotation;

import com.example.commerceplus.common.exception.BusinessException;
import com.example.commerceplus.common.exception.ErrorCode;
import com.example.commerceplus.common.jwt.JwtUser;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j(topic = "argumentResolver")
public class JwtUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAuthAnnotation = parameter.getParameterAnnotation(Auth.class) != null;
        boolean isJwtUserType = parameter.getParameterType().equals(JwtUser.class);

        // Auth 어노테이션 사용하는데 JwtUser 타입이 아님
        if (hasAuthAnnotation != isJwtUserType) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }

        return hasAuthAnnotation;
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, Message<?> message)
            throws Exception
    {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        if(! (authentication.getPrincipal() instanceof JwtUser)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        return (JwtUser) authentication.getPrincipal();
    }
}
