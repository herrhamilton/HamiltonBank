package de.othr.sw.hamilton.utilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

@Configuration
public class AuthUtilities {

    @Value("${application-config.user-password-salt}")
    private static final String salt = "GingerOrange";

    @Bean
    @Scope(value = BeanDefinition.SCOPE_SINGLETON)
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(15, new SecureRandom(salt.getBytes()));
    }
}
