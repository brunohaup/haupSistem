package com.haupsystem.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haupsystem.model.Compra;
import com.haupsystem.model.CompraDto;
import com.haupsystem.service.CompraService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/compra")
@RequiredArgsConstructor
public class CompraController {
	
	@Autowired
    private final CompraService compraService;

    // GET - Lista apenas as compras do usuário logado
    @GetMapping
    public ResponseEntity<List<Compra>> listarComprasUsuarioLogado() {
        return ResponseEntity.ok(compraService.listarComprasUsuarioLogado());
    }

    // POST - Cadastra uma nova compra
    @PostMapping
    public ResponseEntity<Void> criarCompra(@Valid @RequestBody CompraDto compra) {
    	System.out.println(compra.getDescricao());
        compraService.salvar(compra);
        return ResponseEntity.noContent().build();
    }
}
