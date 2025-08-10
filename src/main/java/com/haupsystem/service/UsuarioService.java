package com.haupsystem.service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.haupsystem.model.ProfileEnum;
import com.haupsystem.model.Usuario;
import com.haupsystem.model.UsuarioCreateDTO;
import com.haupsystem.model.UsuarioUpdateDTO;
import com.haupsystem.repository.RepositorioUsuario;
import com.haupsystem.security.UserSpringSecurity;
import com.haupsystem.service.exception.AuthorizationException;
import com.haupsystem.service.exception.DataBindingViolationException;
import com.haupsystem.service.exception.ObjectNotFoundException;

@Service
public class UsuarioService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    public Usuario findById(Long id) {
        UserSpringSecurity UsuarioSpringSecurity = authenticated();
        if (!Objects.nonNull(UsuarioSpringSecurity)
                || !UsuarioSpringSecurity.hasRole(ProfileEnum.ADMIN) && !id.equals(UsuarioSpringSecurity.getId()))
            throw new AuthorizationException("Acesso negado!");

        Optional<Usuario> Usuario = this.repositorioUsuario.findById(id);
        return Usuario.orElseThrow(() -> new ObjectNotFoundException(
                "Usuário não encontrado! Id: " + id + ", Tipo: " + Usuario.class.getName()));
    }

    @Transactional
    public Usuario create(Usuario obj) {
        obj.setId(null);
        obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
        obj = this.repositorioUsuario.save(obj);
        return obj;
    }

    @Transactional
    public Usuario update(Usuario obj) {
        Usuario newObj = findById(obj.getId());
        newObj.setPassword(obj.getPassword());
        newObj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        return this.repositorioUsuario.save(newObj);
    }

    public void delete(Long id) {
        findById(id);
        try {
            this.repositorioUsuario.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possível excluir pois há entidades relacionadas!");
        }
    }

    public static UserSpringSecurity authenticated() {
        try {
            return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }
    
    public Usuario fromDTO(@Valid UsuarioCreateDTO obj) {
    	Usuario user = new Usuario();
        user.setUsername(obj.getUsername());
        user.setPassword(obj.getPassword());
        return user;
    }

    public Usuario fromDTO(@Valid UsuarioUpdateDTO obj) {
    	Usuario user = new Usuario();
        user.setId(obj.getId());
        user.setPassword(obj.getPassword());
        return user;
    }
	
}
