package org.helha.be.sortieappbackend.configuration;


import org.helha.be.sortieappbackend.security.JWTFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeRequests(authorizeRequests -> {
                    //AutentificationController
                    authorizeRequests.requestMatchers("/auth/login").permitAll();
                    authorizeRequests.requestMatchers("/auth/refresh-token").authenticated();
                    //AutorisationController
                    authorizeRequests.requestMatchers("/Autorisations/**").hasAnyRole("ADMIN", "RESPONSIBLE");
                    //QRCodeController
                    authorizeRequests.requestMatchers("/qrcodes/generateFromUser").hasRole("STUDENT");
                    authorizeRequests.requestMatchers("/qrcodes/{id}").hasAnyRole("SUPERVISOR", "LOCAL_ADMIN", "RESPONSIBLE");
                    //RoleController
                    authorizeRequests.requestMatchers("/roles/**").hasAnyRole("ADMIN", "RESPONSIBLE", "LOCAL_ADMIN");
                    //SchoolController
                    authorizeRequests.requestMatchers("/schools/**").hasAnyRole("ADMIN", "RESPONSIBLE", "LOCAL_ADMIN");
                    //UserController
                    authorizeRequests.requestMatchers("/users/profile","/users/activate-form**", "/users/activate", "/users/set-password").permitAll();
                    authorizeRequests.requestMatchers("/users/**").hasAnyRole("ADMIN", "RESPONSIBLE", "LOCAL_ADMIN");

                    //SWAGGER
                    authorizeRequests.requestMatchers("/swagger-ui/**","/v3/api-docs").permitAll();
                    authorizeRequests.anyRequest().authenticated();
                }).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuiler = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuiler.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuiler.build();
    }
}
