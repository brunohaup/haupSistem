package com.haupsystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haupsystem.model.Usuario;
import com.haupsystem.service.UsuarioService;

@RestController
@RequestMapping("usuario")
public class UsuarioController {
	
	@Autowired
	UsuarioService usuarioService;

	@GetMapping("/listar")
	public ResponseEntity<List<Usuario>> listar() {
		return ResponseEntity.ok().body(this.usuarioService.listar());
	}
	
	@PostMapping("update")
	public ResponseEntity<Usuario> update(@RequestBody Usuario usuario) {
		Usuario salvo = this.usuarioService.update(usuario);
	    return ResponseEntity.ok(salvo);
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		this.usuarioService.delete(id);
		return ResponseEntity.noContent().build();
	}
	
}
