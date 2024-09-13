package com.vega.praksa.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class ResourceConflictException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1791564636123821405L;

    private final Long resourceId;

    public ResourceConflictException(Long resourceId, String message) {
        super(message);
        this.resourceId = resourceId;
    }

}
