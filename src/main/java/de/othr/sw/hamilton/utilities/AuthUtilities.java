package de.othr.sw.hamilton.utilities;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

@Configuration
public class AuthUtilities {

    //@Value("{#environment.USER_PASSWORD_SALT}")
    //@Value("${application-config.user-password-salt}")
    //TODO move salt outside code when working
    private static final String salt ="saltlol";

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(15, new SecureRandom(salt.getBytes()));
    }
}
