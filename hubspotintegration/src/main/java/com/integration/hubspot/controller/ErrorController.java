package com.integration.hubspot.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping("/login")
    public String loginError(HttpServletRequest request, Model model) {
        Object error = request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        model.addAttribute("error", error);
        return "login"; // ou retorne um JSON ou String simples se não tiver HTML
    }
}
