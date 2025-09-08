package com.haupsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompraItemArquivoDto {
	
	private Long id;
	
	private Long idItem;
    private String nomeItem;
    
    private Long idArquivo;
    private String nomeArquivo;
    private Long tamanhoArquivo;
    
}
