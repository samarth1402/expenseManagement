package com.adobe.prj;



import com.adobe.prj.config.JWTAuthenticationFilter;
import com.adobe.prj.config.JWTTokenHelper;
import com.adobe.prj.config.RestAuthenticationEntryPoint;
import com.adobe.prj.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private JWTTokenHelper jWTTokenHelper;

    @Autowired
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public MyUserDetailsService userDetailsService() {
        return new MyUserDetailsService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().antMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/resources/**",
                "/static/**",
                "/css/**",
                "/js/**",
                "/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/manager").hasAuthority("MANAGER")
                .antMatchers("/api/approval/**").hasAuthority("MANAGER")
                .antMatchers("/api/project/list/**").hasAnyAuthority("USER", "MANAGER")
                .antMatchers("/api/project/**").hasAuthority("MANAGER")
                .antMatchers("/api/client/**").hasAuthority("MANAGER")
                .antMatchers("/api/user/**").hasAuthority("MANAGER")
                .antMatchers("/api/expenseSheet/**").hasAnyAuthority("USER", "MANAGER")
                .antMatchers("/login").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
                .anyRequest().authenticated().and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint).and()
                .addFilterBefore(new JWTAuthenticationFilter(userDetailsService(), jWTTokenHelper),
                        UsernamePasswordAuthenticationFilter.class);
    }

}
