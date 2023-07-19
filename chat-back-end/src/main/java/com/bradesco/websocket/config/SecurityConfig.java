package com.bradesco.websocket.config;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Collection;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(registry -> registry
                    .requestMatchers("/ws/**").permitAll()
                    .anyRequest().hasRole("USER")
            ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .oauth2ResourceServer(oauth2Configurer -> oauth2Configurer.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwt -> {
                Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
                Collection<String> roles = realmAccess.get("roles");

                var teste = roles.toArray();
                var grantedAuthorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList();
                return new JwtAuthenticationToken(jwt, grantedAuthorities);
            })));

    return http.build();
    }

    @Bean
    Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080")
                .realm("websocket")
                .clientId("admin-cli")
                .grantType(OAuth2Constants.PASSWORD)
                .username("admin")
                .password("admin")
                .build();
    }

}