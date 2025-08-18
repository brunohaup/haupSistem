package com.haupsystem.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.haupsystem.model.Usuario.TipoUsuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsuarioCreateDTO {

    @NotBlank
    @Size(min = 2, max = 100)
    private String userName;

    @NotBlank
    @Size(min = 8, max = 60)
    private String password;
    
    @NotBlank
    @Size(max = 100)
    private String nome;
    
    @Size(max = 255)
    private String email;
    
    @Size(max = 14)
    private String identificador;
    
    private TipoUsuario tipo;

}
