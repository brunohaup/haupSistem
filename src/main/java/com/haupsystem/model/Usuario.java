package com.haupsystem.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;

@Entity
@Table(name="usuario")
@Data
public class Usuario {
	
	public enum TipoUsuario {
		ADMIN, USUARIO;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "username", length = 100, nullable = false, unique = true)
    @Size(min = 2, max = 100)
    @NotBlank
    private String username;
	
    @Column(name = "password", length = 60, nullable = false)
    @JsonProperty(access = Access.WRITE_ONLY)
    @Size(min = 8, max = 60)
    @NotBlank
    private String password;
    
	@Column(name="email", length = 100, nullable = true)
	private String email;
	
	@Column(name="nome", length = 100, nullable = false)
	@NotBlank
	private String nome;
	
	@Column(name="identificador", length = 14, nullable = true)
	private String identificador;
	
	@Column(name="ativa", nullable = false)
	private Boolean ativa;
	
	@Column(name = "ultimoLoginEm")
    private Instant ultimoLoginEm;
	
	@Column(name = "tipo")
	private TipoUsuario tipo;
	
    /*@Column(name = "profile", nullable = false)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_profile")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Integer> profiles = new HashSet<>();

    public Set<ProfileEnum> getProfiles() {
        return this.profiles.stream().map(x -> ProfileEnum.toEnum(x)).collect(Collectors.toSet());
    }

    public void addProfile(ProfileEnum profileEnum) {
        this.profiles.add(profileEnum.getCode());
    }*/
	
}
