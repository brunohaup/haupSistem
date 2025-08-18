package com.haupsystem.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haupsystem.model.TokenAtualizacao;

@Repository
public interface RepositorioTokenAtualizacao extends JpaRepository<TokenAtualizacao, Long> {
	Optional<TokenAtualizacao> findByTokenAndRevogadoFalse(String token);
}
