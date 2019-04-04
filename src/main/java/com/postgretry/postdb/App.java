package com.postgretry.postdb;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan(basePackages="com.postgretry")
public class App
{
	
    public static void main( String[] args )
    {
    	SpringApplication.run(App.class,args );
        //System.out.println( "Hello World!" );
    }
    
   
    
    
}
