package com.haupsystem.model;

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
@Table(name="compraItemArquivo")
@Data
public class CompraItemArquivo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable=false)
	private Long id;
	
	@ManyToOne(targetEntity=CompraItem.class)
    @JoinColumn(name="idCompraItem", nullable=false)
	private CompraItem compraItem;
	
	@ManyToOne(targetEntity=Arquivo.class)
    @JoinColumn(name="idArquivo", nullable=false)
	private Arquivo arquivo;
	
}
