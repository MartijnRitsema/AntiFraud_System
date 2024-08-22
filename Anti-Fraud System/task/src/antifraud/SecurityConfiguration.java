package antifraud;

import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                .csrf().disable()
                .exceptionHandling(handing -> handing
                        .authenticationEntryPoint(restAuthenticationEntryPoint())
                )
                .headers(headers -> headers.frameOptions().disable())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                        .requestMatchers("/api/auth/list").hasAnyRole("ADMINISTRATOR", "SUPPORT")
                        .requestMatchers(HttpMethod.DELETE, "/api/auth/user/**").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/auth/role/**").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/auth/access/**").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction/**").hasRole("MERCHANT")
                        .requestMatchers("/api/antifraud/suspicious-ip/**").hasAnyRole("USER", "SUPPORT")
                        .requestMatchers("/api/antifraud/stolencard/**").hasAnyRole("USER", "SUPPORT")
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll()
                        .anyRequest().authenticated()

                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
