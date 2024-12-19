package com.traders.exchange.exception;

import com.traders.common.appconfig.rest.ProblemDetailWithCause;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class AttentionAlertException extends ErrorResponseException {

    private static final long serialVersionUID = 34353434L;

    private final String entityName;

    private final String errorKey;


    public AttentionAlertException(String defaultMessage, String entityName, String errorKey) {
        super(
            HttpStatus.PRECONDITION_FAILED,
            ProblemDetailWithCause.ProblemDetailWithCauseBuilder.instance()
                .withStatus(HttpStatus.PRECONDITION_FAILED.value())
                .withTitle(defaultMessage)
                .withProperty("message", "error." + errorKey)
                .withProperty("params", entityName)
                .build(),
            null
        );
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public ProblemDetailWithCause getProblemDetailWithCause() {
        return (ProblemDetailWithCause) this.getBody();
    }
}
