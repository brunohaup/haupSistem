package com.haupsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsuarioUpdateDTO {

    private Long id;
    private String password;
	private String email;
	private String nome;
	private String identificador;
	private Boolean ativa;

}
