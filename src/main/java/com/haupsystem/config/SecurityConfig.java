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
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
    	        // Permite POST anônimo para login e cadastro
    	        .requestMatchers(publicPostPermitAllMatchers).permitAll()
    	        // Exige ADMIN para os outros POSTs
    	        .requestMatchers(publicPostAdminMatchers).hasRole("ADMIN")
    	        // públicos (qualquer método)
    	        .requestMatchers(publicAnyMethodMatchers).permitAll()
    	        // resto precisa estar autenticado
    	        .anyRequest().authenticated()
    	    )
            // AuthenticationManager usado pelos filtros
            .authenticationManager(this.authenticationManager)
            // Se os teus filtros custom estendem as classes padrão, podes manter addFilter(...)
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
    CorsConfigurationSource corsConfigurationSource() {
    	CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}