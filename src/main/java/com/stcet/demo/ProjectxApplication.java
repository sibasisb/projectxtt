package com.stcet.demo;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
//import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Configuration;

import com.stcet.model.Subject;
import com.stcet.spring.config.AppConfig;
import com.stcet.spring.dao.SubjectsDAO;

@Configuration
@ComponentScan(basePackages = {
        "com.stcet.demo.Controller",
        "com.stcet.model",
        "com.stcet.spring.config",
        "com.stcet.spring.dao"})
@SpringBootApplication
public class ProjectxApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectxApplication.class, args);
		/*AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(AppConfig.class); 
		SubjectsDAO subjectsDAO=context.getBean(SubjectsDAO.class);
		TeachersDAO teachersDAO=context.getBean(TeachersDAO.class);
		SubjectsLoadDAO fobj=context.getBean(SubjectsLoadDAO.class);*/

	}
}
