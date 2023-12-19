package edu.miu.cs.cs544.config;

import ch.qos.logback.core.pattern.Converter;
import edu.miu.cs.cs544.service.UserCheck;
import edu.miu.cs.cs544.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(OAuth2ClientProperties.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
    private UserCheck userService;

    @Autowired
    public void setUserService(UserCheck userService) {
        this.userService = userService;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }
    private static final String[] WHITE_LIST_URLS = {
            "/api/register-or-login",
            "/register*",
            "/registrationConfirm",
            "/savePassword",
            "/registrationConfirm",
            "/savePassword",
            "/login*",
            "/resetPassword*",
            "/resendRegistrationToken",
            "/logout*", "/error*","/oauth2/**","/login/**"
    };



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
               .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(WHITE_LIST_URLS).permitAll()
                                .requestMatchers("/api/**").authenticated()
                )

                .oauth2Login(oauth2Login ->
                oauth2Login
                        .authorizationEndpoint(authorizationEndpoint ->
                                authorizationEndpoint
                                        .baseUri("/auth2/authorization/api-client-oidc")
                        )
        )
                .oauth2Login(Customizer.withDefaults())
                .oauth2Client(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer
                                .jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder()))
                );


        return http.build();
    }


    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation("https://accounts.google.com"); // Replace with your issuer URI
    }
}