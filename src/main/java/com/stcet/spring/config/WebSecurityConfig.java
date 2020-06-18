package com.stcet.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	@Override
	protected void configure(HttpSecurity http)throws Exception{
		http.csrf().disable();
		http.authorizeRequests()
		.antMatchers("/","/index","/css/style.css","/img/logo.png","/js/script.js","/subjectsfirst","/subjectssecond","/subjectsthird","/subjectsfourth","/generate").permitAll()
		.anyRequest().authenticated()
		.and().formLogin()
		.loginPage("/login")
		.permitAll().defaultSuccessUrl("/admin")
		.and()
		.logout().permitAll();
	}
	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		@SuppressWarnings("deprecation")
		UserDetails user=User.withDefaultPasswordEncoder()
				.username("admin")
				.password("cse")
				.roles("USER")
				.build();
		return new InMemoryUserDetailsManager(user);
	}
}
