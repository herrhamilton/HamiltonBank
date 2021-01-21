package de.othr.sw.hamilton.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userService;

    private final AuthUtilities authUtilities;

    private BCryptPasswordEncoder passwordEncoder() {
        return authUtilities.passwordEncoder();
    }

    private static final String[] ALLOW_ACCESS_WITHOUT_AUTHENTICATION = {"/api/**", "/index", "/", "/login", "/registration", "/registration-submit", "/images/**", "/css/**", "/img/**", "/fonts/**"};

    public AuthConfiguration(UserDetailsService userService, AuthUtilities authUtilities) {
        this.userService = userService;
        this.authUtilities = authUtilities;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(ALLOW_ACCESS_WITHOUT_AUTHENTICATION)
                .permitAll().anyRequest().authenticated();
        http
                .formLogin()
                    .loginPage("/login").permitAll()
                    .defaultSuccessUrl("/overview")
                    .failureUrl("/")
                .and()
                    .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/?logout")
                    .deleteCookies("remember-me")
                    .permitAll()
                .and()
                    .rememberMe();
        //TODO test and remove this after deployment
        http.csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
    }
}
