package com.jtbdevelopment.games.jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer
import com.jtbdevelopment.spring.jackson.JacksonModuleCustomization
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import java.time.ZonedDateTime

/**
 * Date: 8/16/2015
 * Time: 9:01 PM
 */
@Component
@CompileStatic
class JSR310JacksonRegistration implements JacksonModuleCustomization {
    @Override
    void customizeModule(final SimpleModule simpleModule) {
        simpleModule.addDeserializer(ZonedDateTime.class, InstantDeserializer.ZONED_DATE_TIME)
        simpleModule.addSerializer(ZonedDateTime.class, ZonedDateTimeSerializer.INSTANCE);
    }
}
