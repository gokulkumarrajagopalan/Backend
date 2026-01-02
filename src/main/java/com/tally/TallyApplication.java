package com.tally;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class TallyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TallyApplication.class, args);
    }

}
