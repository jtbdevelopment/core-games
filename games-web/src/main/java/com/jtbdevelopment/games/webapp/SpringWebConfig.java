package com.jtbdevelopment.games.webapp;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;

/**
 * Date: 10/18/16 Time: 8:07 PM
 */
@ComponentScan(
    basePackages = "com.jtbdevelopment",
    excludeFilters = {
        @Filter(type = FilterType.REGEX,
            pattern = {
                "com.jtbdevelopment.*Integration.*",
                "com.jtbdevelopment.*.*CoreSpringConfiguration"
            }
        )
    }
)
@PropertySource(value = "classpath:twisted.properties", ignoreResourceNotFound = true)
@Order(value = -1)
class SpringWebConfig {

}
