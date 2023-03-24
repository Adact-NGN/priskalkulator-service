package no.ding.pk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .apply(AadWebApplicationConfiguration.)
//                .requiresChannel(channel -> channel.anyRequest().requiresSecure())
//                .authorizeRequests().anyRequest().authenticated()
//                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
//                .and().build();
//    }
}
