package com.haupsystem.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.haupsystem.model.Compra;
import com.haupsystem.model.Usuario;

@Repository
public interface RepositorioCompra extends JpaRepository<Compra, Long>, JpaSpecificationExecutor<Compra> {
    List<Compra> findBySolicitante(Usuario usuario);
    
    Page<Compra> findBySolicitante(Usuario usuario, Pageable pageable);
}
