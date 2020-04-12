package com.stcet.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
@Configuration
@ComponentScan(basePackages = {
		"com.stcet.demo",
        "com.stcet.demo.Controller",
        "com.stcet.model",
        "com.stcet.spring.config",
        "com.stcet.spring.dao"})

public class AppConfig {
	//private AnnotationConfigApplicationContext context;
	@Autowired
	Environment env;
	private JdbcTemplate jdb;
	@Bean
	JdbcTemplate jdbcTemplate() {
		SingleConnectionDataSource ds=new SingleConnectionDataSource();
		System.out.println("database url:"+env.getProperty("spring.datasource.url"));
		System.out.println("database user:"+env.getProperty("spring.datasource.username"));
		System.out.println("database password:"+env.getProperty("spring.datasource.password"));
		ds.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
		ds.setUrl(env.getProperty("spring.datasource.url"));
		ds.setUsername(env.getProperty("spring.datasource.username"));
		ds.setPassword(env.getProperty("spring.datasource.password"));
		jdb=new JdbcTemplate(ds);
		return jdb;
	}
	/*@Bean
	AnnotationConfigApplicationContext context() {
		 context=new AnnotationConfigApplicationContext(getClass());
		 return context;
	}*/
}
