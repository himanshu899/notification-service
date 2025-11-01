package com.example.notification.controller;

import com.example.notification.api.dto.SubscriptionRequest;
import com.example.notification.api.dto.SubscriptionResponse;
import com.example.notification.domain.model.EventType;
import com.example.notification.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST Controller for subscription management
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
@Validated
@Slf4j
@RequiredArgsConstructor
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;
    
    @PostMapping
    public Mono<ResponseEntity<SubscriptionResponse>> subscribe(
            @Valid @RequestBody SubscriptionRequest request) {
        
        return subscriptionService.subscribe(request.userId(), request.eventType())
            .map(subscription -> ResponseEntity.ok(
                new SubscriptionResponse(subscription.subscriptionId(), "Subscribed successfully")));
    }
    
    @DeleteMapping
    public Mono<ResponseEntity<Void>> unsubscribe(
            @RequestParam String userId,
            @RequestParam EventType eventType) {
        
        return subscriptionService.unsubscribe(userId, eventType)
            .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
