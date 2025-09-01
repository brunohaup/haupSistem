package com.haupsystem.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haupsystem.model.CompraItem;
import com.haupsystem.model.CompraItemOrcamento;

@Repository
public interface RepositorioCompraItemOrcamento extends JpaRepository<CompraItemOrcamento, Long> {
	
	List<CompraItemOrcamento> findByCompraItem(CompraItem compraItem);
	
	List<CompraItemOrcamento> findByCompraItemId(Long idCompraItem);
    
    void deleteByCompraItem(CompraItem compraItem);
    
}