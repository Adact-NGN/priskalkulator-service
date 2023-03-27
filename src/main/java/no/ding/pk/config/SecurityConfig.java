package no.ding.pk.config;

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;

//@Configuration(proxyBeanMethods = false)
//@EnableWebSecurity
//@EnableMethodSecurity
public class SecurityConfig extends WebSecurityConfiguration {

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
//                .cors().disable()
//                .apply(configurer);
//    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .apply(AadWebApplicationConfiguration.aadWebApplication())
//                .and()
//                .authorizeRequests().anyRequest().authenticated()
//                .and()
//                .authorizeHttpRequests().anyRequest().authenticated()
//                .and()
//                .oauth2Login().userInfoEndpoint().oidcUserService(oidcUserService)
//                .and().build();
//    }
}
