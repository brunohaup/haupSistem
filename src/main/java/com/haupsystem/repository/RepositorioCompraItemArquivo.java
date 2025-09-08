package com.haupsystem.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haupsystem.model.CompraItemArquivo;

@Repository
public interface RepositorioCompraItemArquivo extends JpaRepository<CompraItemArquivo, Long> {
	
	List<CompraItemArquivo> findByCompraItemId(Long idCompraItem);
	
}