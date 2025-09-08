package com.haupsystem.model;

import java.math.BigInteger;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompraItemOrcamentoDto {
	
	private Long id;
	
	private Long idCompraItem;
	
	private Date dataHoraInclusao;
	
	private String fornecedor;
	
	private String observacoes;
	
	private BigInteger precoUnitario;
	
	private Long prazoEntrega;
    
}
