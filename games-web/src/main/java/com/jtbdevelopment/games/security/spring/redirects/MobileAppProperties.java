package com.jtbdevelopment.games.security.spring.redirects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Date: 6/13/15 Time: 7:55 PM
 */
@Component
public class MobileAppProperties {

    @Value("${mobile.success.url:/api/security}")
    private String mobileSuccessUrl;
    @Value("${mobile.failure.url:#/app/signin}")
    private String mobileFailureUrl;

    public String getMobileSuccessUrl() {
        return mobileSuccessUrl;
    }

    public void setMobileSuccessUrl(String mobileSuccessUrl) {
        this.mobileSuccessUrl = mobileSuccessUrl;
    }

    public String getMobileFailureUrl() {
        return mobileFailureUrl;
    }

    public void setMobileFailureUrl(String mobileFailureUrl) {
        this.mobileFailureUrl = mobileFailureUrl;
    }
}
