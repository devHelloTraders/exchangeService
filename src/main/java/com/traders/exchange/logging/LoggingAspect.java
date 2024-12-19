package com.traders.exchange.logging;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.env.Environment;

/**
 * Aspect for logging execution of service and repository Spring components.
 *
 * By default, it only runs with the "dev" profile.
 */
@Aspect
public class LoggingAspect extends com.traders.common.logging.LoggingAspect {

    public LoggingAspect(Environment env) {
        super(env);
    }
}
