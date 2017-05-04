package com.jtbdevelopment.games.webapp

import groovy.transform.CompileStatic
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.PropertySource
import org.springframework.core.annotation.Order

/**
 * Date: 10/18/16
 * Time: 8:07 PM
 */
@Configuration
@ComponentScan(
        basePackages = "com.jtbdevelopment",
        excludeFilters = [
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = [
                                "com.jtbdevelopment.*Integration.*",
                                "com.jtbdevelopment.*.*CoreSpringConfiguration",
                        ]
                )])
@PropertySource(value = "classpath:twisted.properties", ignoreResourceNotFound = true)
@CompileStatic
@Order(value = -1)
class SpringWebConfig {
}
