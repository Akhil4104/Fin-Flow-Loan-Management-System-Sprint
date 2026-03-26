package com.finflow.notification_service.dto;

import java.io.Serializable;

public class NotificationEvent implements Serializable {
    private Long userId;
    private String message;
    private String type;

    public NotificationEvent(Long userId, String message, String type) {
        this.userId = userId;
        this.message = message;
        this.type = type;
    }
    public NotificationEvent(){}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
