package com.haupsystem.model;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class OrcamentoDto {
    
    private Long id;
    
    @NotNull(message = "ID do item de compra é obrigatório")
    private Long idCompraItem;
    
    @NotBlank(message = "Fornecedor é obrigatório")
    private String fornecedor;
    
    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser maior que zero")
    private BigDecimal precoUnitario;
    
    @NotNull(message = "Prazo de entrega é obrigatório")
    @Positive(message = "Prazo deve ser maior que zero")
    private Long prazoEntrega;
    
    private String observacoes;
    
}
