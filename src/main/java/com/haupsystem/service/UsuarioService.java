package com.haupsystem.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haupsystem.model.Usuario;
import com.haupsystem.repository.RepositorioUsuario;

@Service
public class UsuarioService {
	
	@Autowired
	private RepositorioUsuario repositorioUsuario;
	
	public Usuario load(Long id) {
		if (id == null) { throw new RuntimeException("Sem ID"); }
		Optional<Usuario> usuario = this.repositorioUsuario.findById(id);
		return usuario.orElseThrow(() -> new RuntimeException("Usuário (ID:"+ id +") não encontrado"));
	}
	
	public List<Usuario> listar() {
		return this.repositorioUsuario.findAll();
	}
	
	@Transactional
	public Usuario update(Usuario usuario) {
	    if (usuario.getId() == null) {
	        // Inserção
	        return repositorioUsuario.save(usuario);
	    } else {
	        // Atualização
	        Usuario existente = load(usuario.getId());
	        existente.setEmail(usuario.getEmail());
	        existente.setSenha(usuario.getSenha());
	        existente.setNome(usuario.getNome());
	        existente.setIdentificador(usuario.getIdentificador());
	        return repositorioUsuario.save(existente);
	    }
	}
	
	public void delete(Long id) {
		load(id);
		try {
			this.repositorioUsuario.deleteById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
