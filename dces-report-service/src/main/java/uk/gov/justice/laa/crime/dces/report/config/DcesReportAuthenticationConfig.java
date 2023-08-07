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
    public static final String API_PATH_CONTRIBUTIONS = "/api/internal/v1/dces/report/contributions/**";
    public static final String API_PATH_FDC = "/api/internal/v1/dces/report/fdc/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(
                authorizeHttpRequests -> authorizeHttpRequests
                    .requestMatchers("/token/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/api-docs/**").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    .requestMatchers(HttpMethod.GET, API_PATH_FDC).access(
                        (authentication, context) ->
                            new AuthorizationDecision(isRequestFromLocal(context.getRequest()))
                    )
                    .requestMatchers(HttpMethod.GET, API_PATH_CONTRIBUTIONS).access(
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
        IpAddressMatcher hasIpAddress = new IpAddressMatcher("127.0.0.1");
        IpAddressMatcher hasIp6Address = new IpAddressMatcher("::1");

        return hasIpAddress.matches(request) || hasIp6Address.matches(request);
    }
}