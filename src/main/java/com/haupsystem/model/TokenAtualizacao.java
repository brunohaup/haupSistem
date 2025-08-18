package com.haupsystem.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "tokenAtualizacao")
@Data
public class TokenAtualizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @Column(name = "token", nullable = false, unique = true, length = 512)
    private String token;

    @Column(name = "expiraEm", nullable = false)
    private Instant expiraEm;

    @Column(name = "revogado", nullable = false)
    private Boolean revogado = false;

    @Column(name = "criadoEm", nullable = false)
    private Instant criadoEm = Instant.now();
}

