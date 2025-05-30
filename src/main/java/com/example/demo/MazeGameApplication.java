package com.example.demo;

import com.example.demo.service.MazeSolverService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MazeGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(MazeGameApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(MazeSolverService solver) {
        return args -> {
            String groupName = "MeinTeam";  // hier euren Gruppennamen einsetzen
            solver.solve(groupName);
        };
    }
}

