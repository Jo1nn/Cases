package com.integration.hubspot.config;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StateStorage {

    private final Map<String, Instant> stateMap = new ConcurrentHashMap<>();

    public void save(String state) {
        stateMap.put(state, Instant.now().plusSeconds(300)); // válido por 5 minutos
    }

    public boolean isValid(String state) {
        Instant expiration = stateMap.get(state);
        return expiration != null && Instant.now().isBefore(expiration);
    }

    public void remove(String state) {
        stateMap.remove(state);
    }
}