package com.haupsystem.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.NoArgsConstructor;

@Entity
@Table(name="usuario")
@NoArgsConstructor
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name="superUsuario")
	private Boolean superUsuario;
	
	@Column(name="email", length = 255, nullable = false, unique = true)
	@NotBlank
	private String email;
	
	@JsonProperty(access = Access.READ_WRITE)
	@Column(name="senha", nullable = false)
	@NotBlank
	private String senha;
	
	@Column(name="nome", length = 255, nullable = false)
	@NotBlank
	private String nome;
	
	@Column(name="identificador", length = 14, nullable = true)
	private String identificador;
	
	//GETTERS E SETTERS

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getSuperUsuario() {
		return superUsuario;
	}
	public void setSuperUsuario(Boolean superUsuario) {
		this.superUsuario = superUsuario;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	
}
