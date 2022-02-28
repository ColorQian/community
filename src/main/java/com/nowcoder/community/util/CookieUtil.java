package com.nowcoder.community.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Component
public class CookieUtil {

    public static String getValue(HttpServletRequest request, String name) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null && name != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName()))
                    return cookie.getValue();

            }
        }
        return null;
    }
}
