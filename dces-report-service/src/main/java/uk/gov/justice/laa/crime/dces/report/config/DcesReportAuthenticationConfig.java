package uk.gov.justice.laa.crime.dces.report.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Configuration
@EnableWebSecurity
public class DcesReportAuthenticationConfig {
    public static final String API_REQUEST_PATH = "/api/internal/v1/dces/report/**";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "::1";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(
                authorizeHttpRequests -> authorizeHttpRequests
                    .requestMatchers("/token/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/api-docs/**").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    .requestMatchers(HttpMethod.GET, API_REQUEST_PATH).access(
                        (authentication, context) ->
                            new AuthorizationDecision(isRequestFromLocal(context.getRequest()))
                    )
                    .anyRequest().authenticated()
            )
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        return http.build();
    }

    private boolean isRequestFromLocal(HttpServletRequest request) {
        IpAddressMatcher hasIpAddress = new IpAddressMatcher(LOCALHOST_IPV4);
        IpAddressMatcher hasIp6Address = new IpAddressMatcher(LOCALHOST_IPV6);
        return hasIpAddress.matches(request) || hasIp6Address.matches(request);
    }
}