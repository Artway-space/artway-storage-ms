package space.artway.artwaystorage.config;

import feign.Feign;
import feign.Logger;
import feign.form.spring.SpringFormEncoder;
import feign.jackson.JacksonDecoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.artway.artwaystorage.client.DropboxClient;
import space.artway.artwaystorage.client.GoogleClient;
import space.artway.artwaystorage.client.YandexClient;

@Configuration
public class FeignConfig {

    @Bean
    public DropboxClient dropboxClientConfig(){
        return Feign.builder()
                .client(new OkHttpClient())
                .logger(new Slf4jLogger(DropboxClient.class))
                .logLevel(Logger.Level.FULL)
                .decoder(new JacksonDecoder())
                .encoder(new SpringFormEncoder())
                .target(DropboxClient.class, "https://api.dropboxapi.com");
    }

    @Bean
    public GoogleClient googleClientConfig(){
        return Feign.builder()
                .client(new OkHttpClient())
                .logger(new Slf4jLogger(GoogleClient.class))
                .logLevel(Logger.Level.FULL)
                .decoder(new JacksonDecoder())
                .encoder(new SpringFormEncoder())
                .target(GoogleClient.class, "https://oauth2.googleapis.com");
    }

    @Bean
    public YandexClient yandexClientConfig(){
        return Feign.builder()
                .client(new OkHttpClient())
                .logger(new Slf4jLogger(YandexClient.class))
                .logLevel(Logger.Level.FULL)
                .decoder(new JacksonDecoder())
                .encoder(new SpringFormEncoder())
                .target(YandexClient.class, "https://oauth.yandex.ru/");
    }
}
