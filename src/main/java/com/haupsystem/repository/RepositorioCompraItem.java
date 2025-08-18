package com.haupsystem.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haupsystem.model.Compra;
import com.haupsystem.model.CompraItem;

@Repository
public interface RepositorioCompraItem extends JpaRepository<CompraItem, Long> {
    List<CompraItem> findByCompra(Compra usuario);
}