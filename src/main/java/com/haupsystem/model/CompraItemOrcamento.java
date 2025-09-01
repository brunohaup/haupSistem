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
@Table(name="compraItemOrcamento")
@Data
public class CompraItemOrcamento {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable=false)
	private Long id;
	
	@ManyToOne(targetEntity=CompraItem.class)
    @JoinColumn(name="idCompraItem", nullable=false)
	private CompraItem compraItem;
	
	@Column(name = "dataHoraInclusao", nullable=false)
	private Date dataHoraInclusao;
	
	@Column(name = "fornecedor", nullable=false)
	private String fornecedor;
	
	@Column(name = "observacoes", nullable=true)
	private String observacoes;
	
	@Column(name = "precoUnitario", nullable=false)
	private BigInteger precoUnitario;
	
	@Column(name = "prazoEntrega", nullable=false)
	private Long prazoEntrega;
	
}
