package com.example.demo.config;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.demo.enums.ANSIColor;
import com.example.demo.mapper.HttpMethodColors;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    // A thread-safe map to store start times for each request
    private final ConcurrentHashMap<String, Long> requestStartTimes = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler)
            throws Exception {

        String requestedURI = request.getRequestURI();
        if (requestedURI.startsWith("/css") || requestedURI.startsWith("/js") || requestedURI.startsWith("/images")) {
            return true;
        }
        // Capture the start time of the request
        long startTime = System.currentTimeMillis();
        String requestId = generateRequestId(request); // Generate a unique key for the request
        requestStartTimes.put(requestId, startTime);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = (authentication != null && authentication.isAuthenticated()) ? authentication.getName()
                : "anonymous";
        String method = request.getMethod();
        Logger.getLogger("Request")
                .info(HttpMethodColors.getMethodColor(method) + "User: " + userName + " requested URL: "
                        + request.getRequestURI() + " [" + method + "]" + ANSIColor.RESET.getCode());
        return true;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable ModelAndView modelAndView) throws Exception {
        String requestedURI = request.getRequestURI();
        if (requestedURI.startsWith("/css") || requestedURI.startsWith("/js") || requestedURI.startsWith("/images")) {
            return;
        }
        // Retrieve the start time for the request
        String requestId = generateRequestId(request);
        Long startTime = requestStartTimes.remove(requestId);
        if (startTime != null) {
            // Calculate the elapsed time
            long elapsedTime = System.currentTimeMillis() - startTime;

            // Log the response details with the elapsed time
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = (authentication != null && authentication.isAuthenticated()) ? authentication.getName()
                    : "anonymous";
            String method = request.getMethod();
            Logger.getLogger("Response").info(HttpMethodColors.getMethodColor(method) +
                    "User: " + userName + " completed URL: " +
                    request.getRequestURI() + " [" + method + "] in " + elapsedTime + " ms" +
                    ANSIColor.RESET.getCode());
        }
    }

    /**
     * Generates a unique key for the request based on the session ID and request
     * URI.
     */
    private String generateRequestId(HttpServletRequest request) {
        String sessionId = request.getSession(false) != null ? request.getSession().getId() : "no-session";
        return sessionId + "-" + request.getRequestURI();
    }

}
