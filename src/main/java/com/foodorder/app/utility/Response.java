package com.foodorder.app.utility;

import com.foodorder.app.enums.ResponseStatus;
import lombok.Getter;

@Getter
public class Response {
    private Object data;
    private final ResponseStatus responseStatus;
    private final String message;

    public Response(Object data, ResponseStatus responseStatus, String message) {
        this.data = data;
        this.responseStatus = responseStatus;
        this.message = message;
    }

    public Response(ResponseStatus responseStatus, String message) {
        this.responseStatus = responseStatus;
        this.message = message;
    }

    public Boolean isSuccess() {
        return responseStatus.equals(ResponseStatus.SUCCESS);
    }
}