package com.marcoantonio.dscatalog.config;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {
    
    @Bean//componente do spring criptografia de senha
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
