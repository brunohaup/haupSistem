package com.haupsystem.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.haupsystem.model.Usuario;

@Repository
public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {

	@Transactional(readOnly = true)
    Usuario findByUsername(String username);
	
}
