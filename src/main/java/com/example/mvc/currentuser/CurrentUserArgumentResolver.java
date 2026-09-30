package com.example.mvc.currentuser;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    static final String HEADER_USER_ID = "X-User-Id";
    static final String HEADER_USER_ROLE = "X-User-Role";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserParam.class)
                && parameter.getParameterType().equals(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory)
            throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        String userId = request.getHeader(HEADER_USER_ID);
        if (userId == null || userId.isBlank()) {
            throw new MissingRequestHeaderException("X-User-Id", parameter);
        }
        String role = webRequest.getHeader(HEADER_USER_ROLE);
        if (role == null || role.isBlank()) {
            role = "USER";
        }

        return new CurrentUser(userId, role);
    }

}
