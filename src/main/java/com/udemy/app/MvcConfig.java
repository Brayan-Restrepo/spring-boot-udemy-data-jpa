package com.udemy.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends 	WebMvcConfigurerAdapter{

	/**
	 * Registrar rutas de recursos externos al proyecto. 
	 * Agrega la ruta del recurso a mostrar al cliente
	 * y el recurso interno proyecto
	 */
	/*
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// TODO Auto-generated method stub
		super.addResourceHandlers(registry);
		registry.addResourceHandler("/uploads/**")
		.addResourceLocations("file:/c:/img/");
		
	}*/

	
}
