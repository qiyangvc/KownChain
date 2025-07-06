package knowchain.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
//@ComponentScan({"knowchain.server.mapper", "knowchain.server.controller", "knowchain.server.service"})

public class KnowChainApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnowChainApplication.class, args);
    }

}
