package com.haupsystem.model;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompraItemDto {
	
	private Long id;
    private String nome;
    private String observacoes;
    
    private BigInteger valor;
    private Long quantidade;
    
    private Boolean aprovado;
    private String motivoRecusa;
    
    private Long idOrcamentoSelecionado;
    
}
