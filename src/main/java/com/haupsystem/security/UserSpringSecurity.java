package com.haupsystem.security;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.haupsystem.model.Usuario;
import com.haupsystem.model.Usuario.TipoUsuario;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserSpringSecurity implements UserDetails {

    private static final long serialVersionUID = 803727069499984571L;
    
	private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private String nome;
    private TipoUsuario tipo;
    
    
    private Instant lastLogin;
    private Instant passwordExpiration;
    
    public UserSpringSecurity(Usuario u) {
        this.id = u.getId();
        this.username = u.getUsername();
        this.password = u.getPassword();
        this.lastLogin = u.getUltimoLoginEm();
        
        List<SimpleGrantedAuthority> listaTipoUsurio = new ArrayList<SimpleGrantedAuthority>();
        listaTipoUsurio.add(new SimpleGrantedAuthority(u.getTipo().name()));
        this.authorities = listaTipoUsurio;
        this.tipo = u.getTipo();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
}
