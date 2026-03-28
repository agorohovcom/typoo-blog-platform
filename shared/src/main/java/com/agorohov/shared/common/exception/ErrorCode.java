package com.agorohov.shared.common.exception;

public interface ErrorCode {

    String getCode();

    String getMessage();

    int getHttpStatusCode();
}
