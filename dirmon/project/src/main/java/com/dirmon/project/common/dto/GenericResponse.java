package com.dirmon.project.common.dto;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
    private int statusCode;

    private String message;

    private T data;

    @Override
    public String toString() {
        return "GenericResponse{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public static ResponseEntity<@NonNull GenericResponse<?>> genericResponse(
            HttpStatus httpStatus,
            String message,
            Object data
    ) {
        return ResponseEntity.status(httpStatus)
                .body(new GenericResponse<>(
                        httpStatus.value(),
                        message,
                        data
                ));
    }
}