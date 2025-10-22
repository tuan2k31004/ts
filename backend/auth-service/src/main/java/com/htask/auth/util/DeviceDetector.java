package com.htask.auth.util;

import org.springframework.stereotype.Component;

@Component
public class DeviceDetector {

    public String detectDeviceType(String userAgent) {
        if (userAgent == null) return "UNKNOWN";

        String ua = userAgent.toLowerCase();

        if (ua.contains("mobile") || ua.contains("android") ||
            ua.contains("iphone") || ua.contains("ipod")) {
            return "MOBILE";
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            return "TABLET";
        } else {
            return "DESKTOP";
        }
    }

    public String detectBrowser(String userAgent) {
        if (userAgent == null) return "Unknown";

        String ua = userAgent.toLowerCase();

        if (ua.contains("edg/")) {
            return "Microsoft Edge";
        } else if (ua.contains("chrome/") && !ua.contains("edg/")) {
            return "Google Chrome";
        } else if (ua.contains("firefox/")) {
            return "Mozilla Firefox";
        } else if (ua.contains("safari/") && !ua.contains("chrome/")) {
            return "Safari";
        } else if (ua.contains("opera/") || ua.contains("opr/")) {
            return "Opera";
        } else {
            return "Unknown Browser";
        }
    }

    public String detectOperatingSystem(String userAgent) {
        if (userAgent == null) return "Unknown";

        String ua = userAgent.toLowerCase();

        if (ua.contains("windows nt 10.0")) {
            return "Windows 10/11";
        } else if (ua.contains("windows nt 6.3")) {
            return "Windows 8.1";
        } else if (ua.contains("windows nt 6.2")) {
            return "Windows 8";
        } else if (ua.contains("windows nt 6.1")) {
            return "Windows 7";
        } else if (ua.contains("windows")) {
            return "Windows";
        } else if (ua.contains("mac os x")) {
            return "macOS";
        } else if (ua.contains("android")) {
            return "Android";
        } else if (ua.contains("iphone") || ua.contains("ipad")) {
            return "iOS";
        } else if (ua.contains("linux")) {
            return "Linux";
        } else {
            return "Unknown OS";
        }
    }
}
