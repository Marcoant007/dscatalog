package com.marcoantonio.dscatalog.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private Environment env;

    @Autowired
    private JwtTokenStore tokenStore;

    private static final String[] ROUTES_PUBLIC = { "/oauth/token", "/h2-console/**" , "/products/**"};

    private static final String[] ROUTES_OPERATOR_OR_ADMIN = { "/categories/**" };

    private static final String[] ROUTES_ADMIN = { "/users/**" };

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        // configurar token store
        resources.tokenStore(tokenStore); // com isso o resource server vai decodificar o token e analisar se ta valido
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        // essa configuração é para liberar o h2
        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            http.headers().frameOptions().disable();
        }

        http.authorizeRequests()
            .antMatchers(ROUTES_PUBLIC).permitAll() // quem tiver acessando o vetor de rota publica, permite acesso 
            .antMatchers(HttpMethod.GET, ROUTES_OPERATOR_OR_ADMIN).permitAll() // vou permitir so visualizar(get) quem for do Operator or admin
            .antMatchers(ROUTES_OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN") // só acessa se possuir algum desses atributos
            .antMatchers(ROUTES_ADMIN).hasRole("ADMIN")// só acessa essa rota se for admin
            .anyRequest().authenticated(); // se acessar qualquer outra rota além dessa, só se estiver autenticado
            http.cors().configurationSource(corsConfigurationSource());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
        corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(
                new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

}
