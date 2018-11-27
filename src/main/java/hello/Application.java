package hello;

import hello.gettingstarted.GettingStartedTutorialConfiguration;
import hello.remotepartition.WorkerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}