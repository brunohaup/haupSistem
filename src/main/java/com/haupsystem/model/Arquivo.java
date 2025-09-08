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
@Data
@Table(name="arquivo")
public class Arquivo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id", nullable=false)
	private Long id;

	@Column(name="dataHoraInclusao", nullable=false)
	private Date dataHoraInclusao;
	
	@ManyToOne(targetEntity=Usuario.class)
    @JoinColumn(name="idUsuarioInclusao", nullable=true)
	private Usuario usuarioInclusao;
	
	@Column(name="diretorio", nullable=false, length=200, unique=true)
	private String diretorio;
	
	@Column(name="tamanho", nullable=false)
	private Long tamanho;
	
	@Column(name="nomeOriginal", nullable=false, length=260)
	private String nomeOriginal;
	
	@Column(name="extensao", nullable=false, length=20)
	private String extensao;
	
}
