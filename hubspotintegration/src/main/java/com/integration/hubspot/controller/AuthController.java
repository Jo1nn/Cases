package com.integration.hubspot.controller;

import com.integration.hubspot.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Gera a URL de autorização para redirecionar o usuário ao HubSpot
    @GetMapping("/authorize")
    public void authorize(HttpServletResponse response) throws IOException {
        String authorizationUrl = authService.generateAuthorizationUrl();
        response.sendRedirect(authorizationUrl);
    }

    // Endpoint que receberá o redirect do HubSpot com o code e state
    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code,
                                           @RequestParam("state") String state) {
        System.out.println(">>> Callback recebido com code: " + code + " e state: " + state);

        authService.exchangeCodeForToken(code, state);

        return ResponseEntity.ok("Autenticação concluída com sucesso!");
    }
}
