package com.finflow.admin_service.producer;

import com.finflow.admin_service.config.RabbitConfig;
import com.finflow.admin_service.dto.NotificationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(Long userId, String message, String type) {
        NotificationEvent event = new NotificationEvent(userId, message, type);
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, event);
    }
}
