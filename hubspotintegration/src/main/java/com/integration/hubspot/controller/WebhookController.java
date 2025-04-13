package com.integration.hubspot.controller;

import com.integration.hubspot.webhook.service.WebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    public ResponseEntity<Void> receiveWebhook(@RequestBody List<Map<String, Object>> webhookData) {
        System.out.println("Webhook recebido: " + webhookData);
        webhookService.processWebhook(webhookData);
        return ResponseEntity.ok().build();
    }
}
