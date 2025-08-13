package com.haupsystem.controller;
import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.haupsystem.model.Usuario;
import com.haupsystem.model.UsuarioCreateDTO;
import com.haupsystem.model.UsuarioUpdateDTO;
import com.haupsystem.service.UsuarioService;



@RestController
@RequestMapping("/usuario")
@Validated
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    
    @GetMapping("/carregar/{id}")
    public ResponseEntity<Usuario> carregarPorId(@PathVariable Long id) {
        Usuario obj = this.usuarioService.findById(id);
        return ResponseEntity.ok().body(obj);
    }
    
    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> lista = usuarioService.listar();
        return ResponseEntity.ok().body(lista);
    }
    
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody UsuarioCreateDTO obj) {
    	System.out.println("chegou aqui "+obj);
        Usuario usuario = this.usuarioService.fromDTO(obj);
        Usuario newUsuario = this.usuarioService.create(usuario);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(newUsuario.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody UsuarioUpdateDTO obj, @PathVariable Long id) {
        obj.setId(id);
        Usuario Usuario = this.usuarioService.fromDTO(obj);
        this.usuarioService.update(Usuario);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
