package com.event.Flowvent.service;

import com.event.Flowvent.dto.ClientCreateDto;
import com.event.Flowvent.dto.ClientResponseDto;
import com.event.Flowvent.entity.Client;
import com.event.Flowvent.exception.ClientNotFoundException;
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

    public Client findClientById(Long id){
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    public ClientResponseDto createClient(ClientCreateDto dto) {
        Client client = new Client();
        client.setName(dto.getName());
        client.setEmail(dto.getEmail());

        Client saved = clientRepository.save(client);

        return new ClientResponseDto(
                saved.getId(),
                saved.getName(),
                saved.getEmail()
        );
    }

    public ClientResponseDto updateClient(Long id, ClientCreateDto dto) {
        Client existentClient = findClientById(id);

        existentClient.setName(dto.getName());
        existentClient.setEmail(dto.getEmail());

        Client updatedClient = clientRepository.save(existentClient);

        return new ClientResponseDto(
                updatedClient.getId(),
                updatedClient.getName(),
                updatedClient.getEmail());
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

    public void deleteClient(Long id){
        Client client = findClientById(id);
        clientRepository.delete(client);
    }
}
