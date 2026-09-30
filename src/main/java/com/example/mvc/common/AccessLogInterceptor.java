package com.example.mvc.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AccessLogInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AccessLogInterceptor.class);

    private static final String START_TIME_ATTR = "accessLogStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        request.setAttribute(START_TIME_ATTR, System.nanoTime());
        return true;    // true — продолжить обработку, false — прервать
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        long durationMs = (System.nanoTime() - startTime) / 1_000_000;

        String handlerInfo = "n/a";
        if (handler instanceof HandlerMethod hm) {
            handlerInfo = hm.getBeanType().getSimpleName() + "." + hm.getMethod().getName();
        }

        log.info("access method={} path={} status={} durationMs={} handler={}",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                durationMs,
                handlerInfo);
    }
}