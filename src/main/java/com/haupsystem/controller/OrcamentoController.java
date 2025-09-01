package com.haupsystem.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haupsystem.model.CompraItemOrcamento;
import com.haupsystem.model.OrcamentoDto;
import com.haupsystem.service.OrcamentoService;

@RestController
@RequestMapping("/orcamento")
public class OrcamentoController {

    @Autowired
    private OrcamentoService orcamentoService;

    // GET - Lista orçamentos por item de compra
    @GetMapping("/item/{idCompraItem}")
    public ResponseEntity<List<CompraItemOrcamento>> listarOrcamentosPorItem(@PathVariable Long idCompraItem) {
        List<CompraItemOrcamento> orcamentos = orcamentoService.listarPorItem(idCompraItem);
        return ResponseEntity.ok(orcamentos);
    }

    // POST - Adiciona novo orçamento para um item
    @PostMapping
    public ResponseEntity<CompraItemOrcamento> adicionarOrcamento(@Valid @RequestBody OrcamentoDto orcamentoDto) {
        CompraItemOrcamento orcamento = orcamentoService.adicionarOrcamento(orcamentoDto);
        return ResponseEntity.ok(orcamento);
    }

    // PUT - Atualiza orçamento existente
    @PutMapping("/{id}")
    public ResponseEntity<CompraItemOrcamento> atualizarOrcamento(
            @PathVariable Long id, 
            @Valid @RequestBody OrcamentoDto orcamentoDto) {
        CompraItemOrcamento orcamento = orcamentoService.atualizarOrcamento(id, orcamentoDto);
        return ResponseEntity.ok(orcamento);
    }

    // DELETE - Remove orçamento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerOrcamento(@PathVariable Long id) {
        orcamentoService.removerOrcamento(id);
        return ResponseEntity.noContent().build();
    }
    
    // GET - Relatório comparativo de orçamentos por compra
    @GetMapping("/comparativo/{idCompra}")
    public ResponseEntity<List<Object>> relatorioComparativo(@PathVariable Long idCompra) {
        List<Object> relatorio = orcamentoService.gerarRelatorioComparativo(idCompra);
        return ResponseEntity.ok(relatorio);
    }
}

