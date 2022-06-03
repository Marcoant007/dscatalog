package com.marcoantonio.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private JwtTokenStore tokenStore;

    private static final String[] ROUTES_PUBLIC = {"/oauth/token"};

    private static final String[] ROUTES_OPERATOR_OR_ADMIN = {"/products/**", "/categories/**"};

    private static final String[] ROUTES_ADMIN = {"/users/**"};


    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        //configurar token store
        resources.tokenStore(tokenStore); // com isso o resource server vai decodificar o token e analisar se ta valido
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        .antMatchers(ROUTES_PUBLIC).permitAll() //quem tiver acessando o vetor de rota publica, permite acesso o
        .antMatchers(HttpMethod.GET, ROUTES_OPERATOR_OR_ADMIN).permitAll() // vou permitir so visualizar(get) quem for do Operator or admin
        .antMatchers(ROUTES_OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN") //só acessa se possuir algum desses atributos
        .antMatchers(ROUTES_ADMIN).hasRole("ADMIN")// só acessa essa rota se for admin
        .anyRequest().authenticated(); // se acessar qualquer outra rota além dessa, só se estiver autenticado
    }
    
}
