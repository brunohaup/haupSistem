package com.haupsystem.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haupsystem.model.CompraItem;
import com.haupsystem.model.CompraItemOrcamento;
import com.haupsystem.model.CompraItemOrcamentoDto;
import com.haupsystem.model.OrcamentoDto;
import com.haupsystem.repository.RepositorioCompraItem;
import com.haupsystem.repository.RepositorioCompraItemOrcamento;

@Service
public class OrcamentoService {

    @Autowired
    private RepositorioCompraItemOrcamento repositorioOrcamento;
    
    @Autowired
    private RepositorioCompraItem repositorioCompraItem;

    public List<CompraItemOrcamentoDto> listarPorItem(Long idCompraItem) {
        CompraItem item = repositorioCompraItem.findById(idCompraItem)
            .orElseThrow(() -> new RuntimeException("Item de compra não encontrado"));
        
        List<CompraItemOrcamentoDto> listaDtos = new ArrayList<>();
        
        List<CompraItemOrcamento> listaEntidade = repositorioOrcamento.findByCompraItem(item);
        listaEntidade.forEach(entidade -> listaDtos.add(retornaDto(entidade)));
        
        //System.out.println(listaEntidade);
        
        return listaDtos;
    }

    @Transactional
    public CompraItemOrcamento adicionarOrcamento(OrcamentoDto dto) {
        CompraItem item = repositorioCompraItem.findById(dto.getIdCompraItem())
            .orElseThrow(() -> new RuntimeException("Item de compra não encontrado"));

        // Validações
        if (dto.getFornecedor() == null || dto.getFornecedor().trim().isEmpty()) {
            throw new RuntimeException("Fornecedor é obrigatório");
        }
        if (dto.getPrecoUnitario() == null || dto.getPrecoUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor deve ser maior que zero");
        }
        if (dto.getPrazoEntrega() == null || dto.getPrazoEntrega() <= 0) {
            throw new RuntimeException("Prazo de entrega deve ser maior que zero");
        }

        CompraItemOrcamento orcamento = new CompraItemOrcamento();
        orcamento.setCompraItem(item);
        orcamento.setFornecedor(dto.getFornecedor());
        orcamento.setPrecoUnitario(dto.getPrecoUnitario().toBigInteger());
        orcamento.setPrazoEntrega(dto.getPrazoEntrega());
        orcamento.setObservacoes(dto.getObservacoes());
        orcamento.setDataHoraInclusao(new Date());

        return repositorioOrcamento.save(orcamento);
    }

    @Transactional
    public CompraItemOrcamento atualizarOrcamento(Long id, OrcamentoDto dto) {
        CompraItemOrcamento orcamento = repositorioOrcamento.findById(id)
            .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        orcamento.setFornecedor(dto.getFornecedor());
        orcamento.setPrecoUnitario(dto.getPrecoUnitario().toBigInteger());
        orcamento.setPrazoEntrega(dto.getPrazoEntrega());
        orcamento.setObservacoes(dto.getObservacoes());

        return repositorioOrcamento.save(orcamento);
    }

    @Transactional
    public void removerOrcamento(Long id) {
        CompraItemOrcamento orcamento = repositorioOrcamento.findById(id)
            .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
        repositorioOrcamento.delete(orcamento);
    }

    public List<Object> gerarRelatorioComparativo(Long idCompra) {
        List<CompraItem> itens = repositorioCompraItem.findByCompraId(idCompra);
        List<Object> relatorio = new ArrayList<>();

        for (CompraItem item : itens) {
            List<CompraItemOrcamento> orcamentos = repositorioOrcamento.findByCompraItem(item);
            
            if (!orcamentos.isEmpty()) {
                Map<String, Object> itemRelatorio = new HashMap<>();
                itemRelatorio.put("item", item.getNome());
                itemRelatorio.put("quantidade", item.getQuantidade());
                
                // Encontra menor e maior preço
                BigDecimal menorPreco = orcamentos.stream()
                    .map(o -> new BigDecimal(o.getPrecoUnitario()))
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
                    
                BigDecimal maiorPreco = orcamentos.stream()
                    .map(o -> new BigDecimal(o.getPrecoUnitario()))
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);

                itemRelatorio.put("menorPreco", menorPreco);
                itemRelatorio.put("maiorPreco", maiorPreco);
                itemRelatorio.put("economia", maiorPreco.subtract(menorPreco));
                itemRelatorio.put("totalOrcamentos", orcamentos.size());
                
                // Lista de fornecedores
                List<String> fornecedores = orcamentos.stream()
                    .map(CompraItemOrcamento::getFornecedor)
                    .collect(Collectors.toList());
                itemRelatorio.put("fornecedores", fornecedores);
                
                relatorio.add(itemRelatorio);
            }
        }

        return relatorio;
    }
    
    public CompraItemOrcamentoDto retornaDto(CompraItemOrcamento orcamento) {
    	
    	CompraItemOrcamentoDto dto = new CompraItemOrcamentoDto();
    	dto.setId(orcamento.getId());
    	dto.setDataHoraInclusao(orcamento.getDataHoraInclusao());
    	dto.setFornecedor(orcamento.getFornecedor());
    	dto.setIdCompraItem(orcamento.getCompraItem().getId());
    	dto.setObservacoes(orcamento.getObservacoes());
    	dto.setPrazoEntrega(orcamento.getPrazoEntrega());
    	dto.setPrecoUnitario(orcamento.getPrecoUnitario());
    	
    	return dto;
    }
    
    public void retornaListaFornecedores() {
    	 
    }
    
}
