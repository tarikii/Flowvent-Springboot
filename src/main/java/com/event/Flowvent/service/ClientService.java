package com.event.Flowvent.service;

import com.event.Flowvent.dto.ClientCreateDto;
import com.event.Flowvent.dto.ClientResponseDto;
import com.event.Flowvent.entity.Client;
import com.event.Flowvent.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client createClient(ClientCreateDto dto) {
        Client client = new Client();
        client.setName(dto.getName());
        client.setEmail(dto.getEmail());
        return clientRepository.save(client);
    }

    public ClientResponseDto updateClient(Long id, ClientCreateDto dto) {
        Client existentClient = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + id));

        existentClient.setName(dto.getName());
        existentClient.setEmail(dto.getEmail());

        Client updatedClient = clientRepository.save(existentClient);

        return new ClientResponseDto(updatedClient.getId(), updatedClient.getName(), updatedClient.getEmail());
    }

    public List<ClientResponseDto> listAllClients() {
        return clientRepository.findAll().stream()
                .map(client -> new ClientResponseDto(
                        client.getId(),
                        client.getName(),
                        client.getEmail()
                ))
                .collect(Collectors.toList());
    }

    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Client not found with ID: " + id);
        }
        clientRepository.deleteById(id);
    }
}
