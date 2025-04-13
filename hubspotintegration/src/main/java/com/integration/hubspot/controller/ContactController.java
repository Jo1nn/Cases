package com.integration.hubspot.controller;

import com.integration.hubspot.contact.dto.ContactListResponse;
import com.integration.hubspot.contact.dto.ContactCreateRequest;
import com.integration.hubspot.contact.dto.ContactResponse;
import com.integration.hubspot.exception.ContactCreationException;
import com.integration.hubspot.contact.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/hubspot/contacts")
@RequiredArgsConstructor
public class ContactController {

    @Autowired
    private ContactService contactService;

    // Criar um novo contato
    @PostMapping("/create")
    public ResponseEntity<Void> createContact(@RequestBody ContactCreateRequest contactCreateRequest) {
        System.out.println(">>> [POST] /create - Requisição recebida:");
        System.out.println(contactCreateRequest);

        try {
            contactService.createOrUpdateContact(contactCreateRequest);
            System.out.println(">>> Contato enviado com sucesso para o serviço.");
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (ContactCreationException e) {
            System.out.println(">>> ERRO ao criar contato: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao criar o contato", e);
        }
    }

    // Atualização de contato por ID
    @PutMapping("/contacts/{id}")
    public ResponseEntity<String> updateContact(
            @PathVariable Long id,
            @RequestBody ContactCreateRequest request) {

        try {
            contactService.createOrUpdateContact(request);
            return ResponseEntity.ok("Contato atualizado ou criado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar o contato: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public ContactListResponse getAllContacts() {
        System.out.println(">>> [GET] /list - Listando todos os contatos");
        return contactService.getAllContacts();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContactResponse getContact(@PathVariable String id) {
        System.out.println(">>> [GET] /{id} - Buscando contato com ID: " + id);
        return contactService.getContactById(id);
    }
}
