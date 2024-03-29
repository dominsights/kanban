package com.dgsystems.kanban;

import com.dgsystems.kanban.boundary.Context;
import com.dgsystems.kanban.usecases.BoardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final ApplicationContext applicationContext;

    public Application(ApplicationContext applicationContext) {

        this.applicationContext = applicationContext;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void run(String... args) throws Exception {
        BoardRepository boardRepository = applicationContext.getBean(BoardRepository.class);
        Context.initialize(boardRepository);
    }
}
