package com.haupsystem.config;

import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.haupsystem.security.JWTAuthenticationFilter;
import com.haupsystem.security.JWTAuthorizationFilter;
import com.haupsystem.security.JWTUtil;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTUtil jwtUtil;
    
    private static final String[] PUBLIC_MATCHERS = {
        "/"
    };

    // Endpoints POST que qualquer um pode acessar (sem autenticação)
    private static final String[] PUBLIC_MATCHERS_POST_PERMIT_ALL = {
        "/usuario", // Assumindo que é o cadastro de um novo usuário
        "/login"
    };

    // Endpoints POST que exigem o papel de ADMIN
    private static final String[] PUBLIC_MATCHERS_POST_ADMIN = {
        "/compras",
        "/compras/nova"
    };
    
    @Value("${cors.allowed.origins}")
	private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable();

        // AuthenticationManager
        AuthenticationManagerBuilder authManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);

        authManagerBuilder
            .userDetailsService(this.userDetailsService)
            .passwordEncoder(bCryptPasswordEncoder());

        this.authenticationManager = authManagerBuilder.build();

        RequestMatcher[] publicPostPermitAllMatchers = Stream.of(PUBLIC_MATCHERS_POST_PERMIT_ALL)
            .map(path -> new AntPathRequestMatcher(path, HttpMethod.POST.name()))
            .toArray(RequestMatcher[]::new);

        RequestMatcher[] publicPostAdminMatchers = Stream.of(PUBLIC_MATCHERS_POST_ADMIN)
            .map(path -> new AntPathRequestMatcher(path, HttpMethod.POST.name()))
            .toArray(RequestMatcher[]::new);

        RequestMatcher[] publicAnyMethodMatchers = Stream.of(PUBLIC_MATCHERS)
            .map(AntPathRequestMatcher::new)
            .toArray(RequestMatcher[]::new);

        http
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(publicPostPermitAllMatchers).permitAll()
                .requestMatchers(publicPostAdminMatchers).hasRole("ADMIN")
                .requestMatchers(publicAnyMethodMatchers).permitAll()
                .anyRequest().authenticated()
            )
            .authenticationManager(this.authenticationManager)
            .addFilter(new JWTAuthenticationFilter(this.authenticationManager, this.jwtUtil))
            .addFilter(new JWTAuthorizationFilter(this.authenticationManager, this.jwtUtil, this.userDetailsService));

        return http.build();
    }
    
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder())
                .and()
                .build();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
            "https://haup-system.vercel.app",
            "http://localhost:3000" // opcional, para testes locais
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}