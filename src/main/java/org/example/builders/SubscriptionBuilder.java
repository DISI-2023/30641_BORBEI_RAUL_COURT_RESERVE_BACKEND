package org.example.builders;

import org.example.dtos.SubscriptionDTO;
import org.example.entities.Subscription;

public class SubscriptionBuilder {

    public static SubscriptionDTO toSubscriptionDTO(Subscription subscription){
        return SubscriptionDTO.builder().id(subscription.getId())
                .startTime(subscription.getStartTime())
                .endTime(subscription.getEndTime())
                .startHour(subscription.getStartHour())
                .endHour(subscription.getEndHour())
                .dayOfWeek(subscription.getDayOfWeek())
                .type(subscription.getType())
                .finalPrice(subscription.getFinalPrice())
                .userEmail(subscription.getAppUser().getEmail())
                .fieldName(subscription.getField().getName())
                .build();
    }

    public static Subscription toEntity(SubscriptionDTO subscriptionDTO){
        return Subscription.builder().id(subscriptionDTO.getId())
                .startHour(subscriptionDTO.getStartHour())
                .endHour(subscriptionDTO.getEndHour())
                .startTime(subscriptionDTO.getStartTime())
                .endTime(subscriptionDTO.getEndTime())
                .dayOfWeek(subscriptionDTO.getDayOfWeek())
                .type(subscriptionDTO.getType())
                .finalPrice(subscriptionDTO.getFinalPrice())
                .build();
    }
}
