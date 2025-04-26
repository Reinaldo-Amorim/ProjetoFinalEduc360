package br.com.cdb.bancodigital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BancodigitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(BancodigitalApplication.class, args);
    }
}
