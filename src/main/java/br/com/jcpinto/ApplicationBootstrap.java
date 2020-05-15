package br.com.jcpinto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class) 
@SpringBootApplication(scanBasePackages={"br.com.jcpinto"})
public class ApplicationBootstrap {
		
	public static void main(String[] args) {
		SpringApplication.run(ApplicationBootstrap.class, args);
	}

}
