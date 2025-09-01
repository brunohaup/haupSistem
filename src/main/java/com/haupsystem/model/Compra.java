package com.haupsystem.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="compra")
@Data
public class Compra {
	
	public enum EtapaCompra {
		CRIACAO, ORCAMENTO, AGUARDANDO_APROVACAO, APROVADA, APROVADA_PARCIAL, RECUSADA, REALIZADA, FINALIZADA;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable=false)
	private Long id;
	
	@Column(name = "etapa", nullable=false)
	private EtapaCompra etapa;
	
	@ManyToOne(targetEntity=Usuario.class)
    @JoinColumn(name="idUsuario", nullable=false)
	private Usuario solicitante;
	
	@Column(name = "dataHoraInclusao", nullable=false)
	private Date dataHoraInclusao;
	
	@Column(name = "dataHoraInclusaoOrcamento", nullable=true)
	private Date dataHoraInclusaoOrcamento;
	
	@Column(name = "dataHoraCompraAprovada", nullable=true)
	private Date dataHoraCompraAprovada;
	
	@Column(name = "dataHoraCompraRecusada", nullable=true)
	private Date dataHoraCompraRecusada;
	
	@Column(name = "dataHoraFinalizada", nullable=true)
	private Date dataHoraFinalizada;
	
	@Column(name = "ativa", nullable=false)
	private Boolean ativa;
	
	@Column(name = "descricao", length = 100, nullable=false)
	private String descricao;
	
}
