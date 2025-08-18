package com.haupsystem.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haupsystem.model.TokenRevogado;

@Repository
public interface RepositorioTokenRevogado extends JpaRepository<TokenRevogado, Long> {
	boolean existsByJti(String jti);
}
