package com.haupsystem.config;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
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
    
    private static final String[] PUBLIC_MATCHERS_POST = {
        "/usuario",
        "/login",
        "/compras",
        "/compras/nova"
    };

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

        // Converte arrays de String para RequestMatcher[]
        RequestMatcher[] publicPostMatchers = Stream.of(PUBLIC_MATCHERS_POST)
            .map(path -> new AntPathRequestMatcher(path, HttpMethod.POST.name()))
            .toArray(RequestMatcher[]::new);

        RequestMatcher[] publicAnyMethodMatchers = Stream.of(PUBLIC_MATCHERS)
            .map(AntPathRequestMatcher::new) // qualquer método
            .toArray(RequestMatcher[]::new);

        http
        .cors().and()
        .csrf().disable()
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(new AntPathRequestMatcher("/**", "OPTIONS")).permitAll()
            .requestMatchers(publicPostMatchers).permitAll()
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
    CorsConfigurationSource corsConfigurationSource() {
    	CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // aceita qualquer origem
        config.addAllowedHeader("*");        // aceita qualquer header
        config.addAllowedMethod("*");        // aceita qualquer método

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}