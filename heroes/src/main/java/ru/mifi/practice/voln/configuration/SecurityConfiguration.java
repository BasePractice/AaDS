package ru.mifi.practice.voln.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.mifi.practice.voln.domain.entity.AuthorityEntity;
import ru.mifi.practice.voln.repository.AuthorityRepository;
import ru.mifi.practice.voln.repository.UserRepository;
import ru.mifi.practice.voln.service.JwtService;
import ru.mifi.practice.voln.service.UserService;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter filter;
    private final UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers(
                    "/swagger-ui/**", "/swagger-resources/*", "/v3/api-docs/**",
                    "/web/css/**",
                    "/web/js/**",
                    "/web/json/**",
                    "/web/images/**",
                    "/login",
                    "/auth/sign-in", "/auth/sign-up",
                    "/error",
                    "/actuator"
                ).permitAll()
                .anyRequest().authenticated())
            .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(request -> {
            var corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOriginPatterns(List.of("*"));
            corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            corsConfiguration.setAllowedHeaders(List.of("*"));
            corsConfiguration.setAllowCredentials(true);
            return corsConfiguration;
        }));
        http.formLogin(login -> login.defaultSuccessUrl("/web/index.html"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    @EventListener(ApplicationReadyEvent.class)
    private void afterConfigure(ApplicationReadyEvent event) {
        UserRepository repository = event.getApplicationContext().getBean(UserRepository.class);
        var users = repository.findAll();
        users.forEach(System.out::println);
        AuthorityRepository authorityRepository = event.getApplicationContext().getBean(AuthorityRepository.class);
        List<AuthorityEntity> authorities = authorityRepository.findAll();
        authorities.forEach(System.out::println);
    }

    @Component
    @RequiredArgsConstructor
    public static class JwtAuthenticationFilter extends OncePerRequestFilter {
        public static final String BEARER_PREFIX = "Bearer ";
        public static final String HEADER_NAME = "Authorization";
        private final JwtService jwtService;
        private final UserService userService;

        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                        @NonNull FilterChain filterChain
        ) throws ServletException, IOException {

            var authHeader = request.getHeader(HEADER_NAME);
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            var jwt = authHeader.substring(BEARER_PREFIX.length());
            var userId = jwtService.extractUserId(jwt);

            if (SecurityContextHolder.getContext().getAuthentication() == null && userId.isPresent()) {
                UserDetails userDetails = userService.loadUserById(userId.get());
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}
