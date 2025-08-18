package com.haupsystem.security;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private JWTUtil jwtUtil;

    private UserDetailsService userDetailsService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
            UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        /*String authorizationHeader = request.getHeader("Authorization");
        if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            UsernamePasswordAuthenticationToken auth = getAuthentication(token);
            if (Objects.nonNull(auth))
                SecurityContextHolder.getContext().setAuthentication(auth);
            
        }
        filterChain.doFilter(request, response);*/
    	String authorizationHeader = request.getHeader("Authorization");

    	if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
    	    String token = authorizationHeader.substring(7).trim();
    	    UsernamePasswordAuthenticationToken auth = getAuthentication(token);

    	    if (Objects.nonNull(auth)) {
    	        SecurityContextHolder.getContext().setAuthentication(auth);
    	    } else {
    	        // Token inválido ou expirado → retorna 401
    	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    	        response.getWriter().write("Token inválido ou expirado");
    	        response.getWriter().flush();
    	        return; // não continua no filter chain
    	    }
    	} else {
    	    // Sem token → retorna 401
    	    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    	    response.getWriter().write("Token não informado");
    	    response.getWriter().flush();
    	    return;
    	}

    	// Se chegou aqui, o token é válido
    	filterChain.doFilter(request, response);
    }

    /*private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        if (this.jwtUtil.expirado(token)) {
            String username = this.jwtUtil.getUsername(token);
            UserDetails user = this.userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticatedUser = new UsernamePasswordAuthenticationToken(user, null,
                    user.getAuthorities());
            return authenticatedUser;
        }
        return null;
    }*/
    
    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            // Tenta extrair o username; se expirado ou inválido, vai lançar exceção
            String username = this.jwtUtil.getUsername(token);

            UserDetails user = this.userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } catch (ExpiredJwtException e) {
            System.out.println("Token expirado!");
            return null;
        } catch (JwtException e) {
            System.out.println("Token inválido!");
            e.printStackTrace();
            return null;
        }
    }

}
