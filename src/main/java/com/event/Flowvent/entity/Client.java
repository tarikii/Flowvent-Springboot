package com.event.Flowvent.entity;

import com.event.Flowvent.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("client")
    private List<Ticket> tickets;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public Client() {}

    public Client(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
