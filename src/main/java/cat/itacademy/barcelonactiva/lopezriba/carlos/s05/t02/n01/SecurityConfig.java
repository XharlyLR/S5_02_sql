package cat.itacademy.barcelonactiva.lopezriba.carlos.s05.t02.n01;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{

     @Override
        protected void configure(HttpSecurity http) throws Exception{
            http.cors().and().csrf().disable();
        }
}