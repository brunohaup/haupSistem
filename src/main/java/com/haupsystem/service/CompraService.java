package com.haupsystem.service;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.haupsystem.model.Compra;
import com.haupsystem.model.CompraDto;
import com.haupsystem.model.CompraItem;
import com.haupsystem.model.CompraItemDto;
import com.haupsystem.model.Usuario;
import com.haupsystem.repository.RepositorioCompra;
import com.haupsystem.repository.RepositorioCompraItem;
import com.haupsystem.repository.RepositorioUsuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompraService {

	@Autowired
    private final RepositorioCompra repositorioCompra;
	@Autowired
	private final RepositorioUsuario repositorioUsuario;
	@Autowired
	private final RepositorioCompraItem repositorioCompraItem;

    // Lista apenas as compras do usuário logado
    public List<Compra> listarComprasUsuarioLogado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = repositorioUsuario.findByUsername(username);
        if(usuario == null) {
        	new RuntimeException("Usuário não encontrado");
        }
        return repositorioCompra.findBySolicitante(usuario);
    }

    public void salvar(CompraDto compra) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = repositorioUsuario.findByUsername(username);
        if(usuario == null) {
        	new RuntimeException("Usuário não encontrado");
        }
        
        validaDtoInclusaoCompra(compra);
        
        Compra novaCompra = new Compra();
        novaCompra.setSolicitante(usuario);
        novaCompra.setAtiva(true);
        novaCompra.setDataHoraInclusao(new Date());
        
        novaCompra.setDescricao(compra.getDescricao());
        novaCompra.setEtapa(compra.getEtapa());
        
        repositorioCompra.save(novaCompra);
        
        for(CompraItemDto item : compra.getItens()) {
        	
        	CompraItem novoItem = new CompraItem();
        	novoItem.setDataHoraInclusao(new Date());
        	novoItem.setCompra(novaCompra);
        	
        	novoItem.setNome(item.getNome());
        	novoItem.setQuantidade(item.getQuantidade());
        	novoItem.setValor(item.getValor());
        	
        	repositorioCompraItem.save(novoItem);
        	
        }
    }
    
    private void validaDtoInclusaoCompra(CompraDto dto) {
    	
    	if(dto.getDescricao() == null) {
    		new RuntimeException("Descrição não informada");
    	}
    	
    	
    	
    }
    
}
