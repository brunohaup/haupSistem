package com.haupsystem.service;

import java.time.Instant;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.haupsystem.model.TokenAtualizacao;
import com.haupsystem.model.TokenRevogado;
import com.haupsystem.model.Usuario;
import com.haupsystem.repository.RepositorioTokenAtualizacao;
import com.haupsystem.repository.RepositorioUsuario;
import com.haupsystem.security.JWTUtil;
import com.haupsystem.security.UserSpringSecurity;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final AuthenticationManager authManager;
    private final JWTUtil jwt;
    private final RepositorioUsuario usuarioRepo;
    private final RepositorioTokenAtualizacao refreshRepo;
    
    @Transactional
    public Map<String, Object> login(String username, String rawPassword) {
        Usuario u = usuarioRepo.findByUsername(username);
        
        if(u == null) {
        	throw new UsernameNotFoundException("Usuário não encontrado");
        }

        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, rawPassword));
            UserSpringSecurity user = (UserSpringSecurity) auth.getPrincipal();

            // checagens de expiração
            if (!user.isAccountNonExpired()) throw new DisabledException("Conta expirada");
            if (!user.isCredentialsNonExpired()) throw new CredentialsExpiredException("Senha expirada");
            if (!user.isAccountNonLocked()) throw new LockedException("Conta bloqueada");
            if (!user.isEnabled()) throw new DisabledException("Conta desativada");

            String access = jwt.gerarAccessToken(user);
            
            SecurityContextHolder.getContext().setAuthentication(auth);

            // emitir refresh e persistir (rotativo)
            String refreshToken = jwt.gerarRefreshToken(u);
            TokenAtualizacao ta = new TokenAtualizacao();
            ta.setUsuario(u);
            ta.setToken(refreshToken);
            ta.setExpiraEm(jwt.parse(refreshToken).getExpiration().toInstant());
            refreshRepo.save(ta);

            // reset tentativas & atualizar último login
            u.setUltimoLoginEm(Instant.now());
            usuarioRepo.save(u);

            return Map.of(
                    "token", access,
                    "tipo", "Bearer",
                    "expiraEm", jwt.parse(access).getExpiration().toInstant().toString(),
                    "refreshToken", refreshToken
            );
        } catch (AuthenticationException e) {
        	
            throw e;
        }
    }

    @Transactional
    public Map<String, Object> refresh(String oldRefreshToken) {
        // valida assinatura/expiração
        Claims claims = jwt.parse(oldRefreshToken);
        String username = claims.getSubject();

        TokenAtualizacao salvo = refreshRepo.findByTokenAndRevogadoFalse(oldRefreshToken)
                .orElseThrow(() -> new BadCredentialsException("Refresh inválido ou revogado"));

        Usuario u = usuarioRepo.findByUsername(username);
		
        if(u == null) {
        	throw new UsernameNotFoundException("Usuário não encontrado");
        }

        // ROTACIONAR: revoga o antigo e emite novo
        salvo.setRevogado(true);
        refreshRepo.save(salvo);

        String novoRefresh = jwt.gerarRefreshToken(u);
        TokenAtualizacao novo = new TokenAtualizacao();
        novo.setUsuario(u);
        novo.setToken(novoRefresh);
        novo.setExpiraEm(jwt.parse(novoRefresh).getExpiration().toInstant());
        refreshRepo.save(novo);

        // novo access
        UserSpringSecurity user = new UserSpringSecurity(u);
        String novoAccess = jwt.gerarAccessToken(user);

        return Map.of(
                "token", novoAccess,
                "tipo", "Bearer",
                "expiraEm", jwt.parse(novoAccess).getExpiration().toInstant().toString(),
                "refreshToken", novoRefresh
        );
    }

    @Transactional
    public void logout(String refreshToken, @Nullable String accessTokenJti) {
        refreshRepo.findByTokenAndRevogadoFalse(refreshToken).ifPresent(ta -> {
            ta.setRevogado(true);
            refreshRepo.save(ta);
        });

        // opcional: colocar o access atual na blacklist (se você validar jti nos filtros)
        if (accessTokenJti != null) {
            TokenRevogado tr = new TokenRevogado();
            tr.setJti(accessTokenJti);
            // expirar quando o access expirar
            Instant exp = jwt.parse(refreshToken).getExpiration().toInstant(); // ou do access
            tr.setExpiraEm(exp);
        }
    }
}
