package com.haupsystem.model;

import java.math.BigInteger;
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
@Table(name="compraItem")
@Data
public class CompraItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable=false)
	private Long id;
	
	@ManyToOne(targetEntity=Compra.class)
    @JoinColumn(name="idCompra", nullable=false)
	private Compra compra;
	
	@Column(name = "dataHoraInclusao", nullable=false)
	private Date dataHoraInclusao;
	
	@Column(name = "nome", nullable=false)
	private String nome;
	
	@Column(name = "observacoes", nullable=true)
	private String observacoes;
	
	@Column(name = "quantidade", nullable=false)
	private Long quantidade;
	
	@Column(name = "valor", nullable=false)
	private BigInteger valor;
	
	@Column(name = "aprovado", nullable=true)
	private Boolean aprovado;
	
	@Column(name = "motivoRecusa", nullable=true)
	private String motivoRecusa;
	
}
