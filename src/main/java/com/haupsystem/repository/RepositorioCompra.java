package com.haupsystem.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haupsystem.model.Compra;
import com.haupsystem.model.Usuario;

@Repository
public interface RepositorioCompra extends JpaRepository<Compra, Long> {
    List<Compra> findBySolicitante(Usuario usuario);
}