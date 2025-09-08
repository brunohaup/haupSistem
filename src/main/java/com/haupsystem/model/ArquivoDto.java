package com.haupsystem.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArquivoDto {

	private Long id;	
	private Date dataHoraInclusao;
	private Long idUsuarioInclusao;
	private String nomeUsuarioInclusao;
	private String diretorio;	
	private Long tamanho;	
	private String nomeOriginal;	
	private String extensao;
	
}
