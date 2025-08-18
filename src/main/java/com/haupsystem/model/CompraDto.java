package com.haupsystem.model;

import java.util.List;

import com.haupsystem.model.Compra.EtapaCompra;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompraDto {
	
    private String descricao;
	private EtapaCompra etapa;
	
	private List<CompraItemDto> itens;

}
