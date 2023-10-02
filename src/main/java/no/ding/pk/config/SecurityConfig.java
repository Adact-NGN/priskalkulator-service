package no.ding.pk.config;

import com.azure.spring.cloud.autoconfigure.aad.AadResourceServerWebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Profile({"default", "dev", "prod"})
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends AadResourceServerWebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.authorizeHttpRequests()
                .anyRequest().permitAll()
                .and()
                .csrf().disable();
    }

//    @Bean
//    public JwtDecoder jwtDecoder() {
//        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(this.jwkIssuerUri);
//        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(this.jwkIssuerUri);
//        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, this.jwtTokenValidator);
//        jwtDecoder.setJwtValidator(withAudience);
//        return jwtDecoder;
//    }
}