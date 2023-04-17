package no.ding.pk.config;

import org.springframework.beans.factory.annotation.Value;

//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity()
public class SecurityConfig {

    @Value("${app.protect.authenticated}")
    private String[] protectedRoutes;

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);
//
//        http.authorizeHttpRequests()
//                .requestMatchers(protectedRoutes).authenticated()
//                .anyRequest().permitAll();
//    }
}