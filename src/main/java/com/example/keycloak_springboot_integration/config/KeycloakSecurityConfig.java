package com.example.keycloak_springboot_integration.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;


@Configuration
@EnableWebSecurity
@KeycloakConfiguration
public class KeycloakSecurityConfig {

    // Keycloak properties to hold configuration values such as client ID and secret
    @Autowired
    private final KeycloakProperties keycloakProperties;

    // Constructor to initialize Keycloak properties
    public KeycloakSecurityConfig(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }

    /**
     * Configures Keycloak to resolve properties from application.yaml or .env file.
     * This allows us to define Keycloak configurations dynamically without hardcoding values.
     *
     * @return KeycloakSpringBootConfigResolver instance to resolve configurations
     */
    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    /**
     * Defines the session authentication strategy using a registered session authentication strategy.
     * This ensures that sessions are handled correctly within Keycloak's context.
     *
     * @return SessionAuthenticationStrategy configured for session management
     */
    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(sessionRegistry());
    }

    /**
     * Provides a session registry to track sessions. This is essential for Keycloak to manage sessions effectively.
     *
     * @return a SessionRegistry implementation
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * Defines the security filter chain for HTTP security configuration.
     * Configures authorization rules, OAuth2 login settings, and session management.
     *
     * @param http HttpSecurity to configure
     * @return a built SecurityFilterChain instance
     * @throws Exception in case of any configuration error
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization ->
                                authorization.baseUri("/oauth2/authorize"))
                        .redirectionEndpoint(redirection ->
                                redirection.baseUri("/login/oauth2/code/*")))
                .sessionManagement(session -> session
                        .sessionAuthenticationStrategy(sessionAuthenticationStrategy()));

        return http.build();
    }

    /**
     * Configures an authentication manager using the Keycloak authentication provider.
     * This manager is used to process authentication requests and apply Keycloak-specific handling.
     *
     * @param http HttpSecurity to configure
     * @return an AuthenticationManager instance
     * @throws Exception in case of any configuration error
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(keycloakAuthenticationProvider());
        return authenticationManagerBuilder.build();
    }

    /**
     * Creates a KeycloakAuthenticationProvider bean to manage authentication within the application.
     *
     * @return KeycloakAuthenticationProvider instance
     */
    @Bean
    public KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
        return new KeycloakAuthenticationProvider();
    }

    /**
     * Defines a Keycloak client registration for OAuth2 login.
     * This method sets up client details such as ID, secret, scopes, and URIs for authorization, token, and user info.
     *
     * @return ClientRegistration configured for Keycloak
     */
    private ClientRegistration keycloakClientRegistration() {
        return ClientRegistration.withRegistrationId("keycloak")
                .clientId(keycloakProperties.getClientId())
                .clientSecret(keycloakProperties.getClientSecret())
                .scope(keycloakProperties.getScopes())
                .authorizationUri(keycloakProperties.getAuthServerUrl() + "/auth")
                .tokenUri(keycloakProperties.getAuthServerUrl() + "/token")
                .userInfoUri(keycloakProperties.getAuthServerUrl() + "/userinfo")
                .redirectUri(keycloakProperties.getRedirectUri())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();
    }
}