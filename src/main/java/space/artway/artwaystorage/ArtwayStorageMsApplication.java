package space.artway.artwaystorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ArtwayStorageMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArtwayStorageMsApplication.class, args);
    }

}
