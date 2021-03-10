package io.gridmc.currency.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Config {

    /**
     * @return Directory the config will be present in.
     */
    String dir() default "";

    /**
     * @return Config name.
     */
    String name();
}
