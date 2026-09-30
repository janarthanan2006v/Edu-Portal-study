package com.prince.materialportal.util;

import com.prince.materialportal.dto.ApiResponse;

public final class ResponseBuilder {

    private ResponseBuilder() {}

    public static <T> ApiResponse<T> buildSuccess(String message, T data) {
        return new ApiResponse<>(true, message, data, 200);
    }

    public static <T> ApiResponse<T> buildCreated(String message, T data) {
        return new ApiResponse<>(true, message, data, 201);
    }

    public static <T> ApiResponse<T> buildError(String message, int status) {
        return new ApiResponse<>(false, message, null, status);
    }
}
