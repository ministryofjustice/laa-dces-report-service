package uk.gov.justice.laa.crime.dces.report.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class DcesReportAuthenticationConfig {
    public static final String API_PATH_CONTRIBUTIONS = "/api/internal/v1/dces/report/contributions/**";
    public static final String API_PATH_FDC = "/api/internal/v1/dces/report/fdc/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // TODO (DCES-77): confirm what level of authentication security will be required to access this service
//        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
//        http.oauth2Login(Customizer.withDefaults());

        http
            .authorizeHttpRequests(
                authorizeHttpRequests -> authorizeHttpRequests
                    .requestMatchers("/token/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/api-docs/**").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    .requestMatchers(HttpMethod.GET, API_PATH_CONTRIBUTIONS).permitAll()
                    .requestMatchers(HttpMethod.GET, API_PATH_FDC).permitAll()
                    // TODO (DCES-77): if authentication is required leave authenticated(), otherwise maybe change to deny
                    .anyRequest().authenticated()
            )
            .sessionManagement(
                    sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // TODO (DCES-77): confirm what level of authentication security will be required to access this service
//            .oauth2ResourceServer(
//                oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults())
//            )
            ;
        return http.build();
    }
}