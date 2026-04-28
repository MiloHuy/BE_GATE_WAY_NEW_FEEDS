package com.example.identity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;
    private String message;
    private String status;
    private T data;
    static final int SUCCESS = 1000;

    public ApiResponse() {
    }

    public ApiResponse(int code, String message, String status, T data) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.data = data;
    }

    // Static helper methods
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SUCCESS, "Success", "success", data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, "error", null);
    }

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
