package com.haupsystem.model;

import java.util.Date;
import java.util.List;

import com.haupsystem.model.Compra.EtapaCompra;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompraDto {
	
	private Long id;
    private String descricao;
	private EtapaCompra etapa;
	
	private Date dataHoraInclusao;
	private Date dataHoraInclusaoOrcamento;
	private Date dataHoraCompraAprovada;
	private Date dataHoraCompraRecusada;
	private Date dataHoraFinalizada;
	private Boolean ativa;
	
	private Long solicitanteId;
    private String solicitanteNome; 
	
	private List<CompraItemDto> itens;

}
