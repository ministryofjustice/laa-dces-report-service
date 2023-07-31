package uk.gov.justice.laa.crime.dces.report.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class DcesReportAuthenticationConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(
                authorizeHttpRequests -> authorizeHttpRequests
                    .requestMatchers("/token/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/api-docs/**").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
// TODO (DCES-67): if authentication is required, replace .permitAll() with authenticated()
                    .anyRequest().permitAll() //.authenticated()
            )
            .sessionManagement(
                sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
// TODO (DCES-67): confirm what level of authentication security will be required to access this service
//            )
//            .oauth2ResourceServer(
//                (oauth2ResourceServer) ->
//                    oauth2ResourceServer.jwt(Customizer.withDefaults())
        );

        return http.build();
    }
}