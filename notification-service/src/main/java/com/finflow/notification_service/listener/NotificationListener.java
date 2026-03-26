package com.finflow.notification_service.listener;

import com.finflow.notification_service.dto.NotificationEvent;
import com.finflow.notification_service.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private final NotificationService service;

    public NotificationListener(NotificationService service) {
        this.service = service;
    }

    @RabbitListener(queues = "notification-queue")
    public void receiveMessage(NotificationEvent event) {

        service.processNotification(
                event.getUserId(),
                event.getMessage(),
                event.getType()
        );
    }
}
