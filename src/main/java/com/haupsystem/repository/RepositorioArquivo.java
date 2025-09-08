package com.haupsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.haupsystem.model.Arquivo;

@Repository
public interface RepositorioArquivo extends JpaRepository<Arquivo, Long>, JpaSpecificationExecutor<Arquivo> {
    
	
	
}
