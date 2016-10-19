package com.jtbdevelopment.games.dev.utilities.jetty

import groovy.transform.CompileStatic
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.annotation.Order

/**
 * Date: 10/18/16
 * Time: 10:31 PM
 */
@Configuration
@ComponentScan(basePackages = "com.jtbdevelopment")
@CompileStatic
@PropertySource(value = "classpath:/integration.properties")
@Order(value = -1)
class AppConfig {
}
