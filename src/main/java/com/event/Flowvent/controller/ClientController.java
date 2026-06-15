package com.event.Flowvent.controller;

import com.event.Flowvent.dto.ClientCreateDto;
import com.event.Flowvent.dto.ClientResponseDto;
import com.event.Flowvent.entity.Client;
import com.event.Flowvent.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@Valid @RequestBody ClientCreateDto dto) {
        Client newClient = clientService.createClient(dto);
        return ResponseEntity.ok(newClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(@PathVariable Long id, @Valid @RequestBody ClientCreateDto dto) {
        ClientResponseDto updated = clientService.updateClient(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        List<ClientResponseDto> clients = clientService.listAllClients();
        return ResponseEntity.ok(clients);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
