package space.artway.artwaystorage.config;

import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
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
                .encoder(new JacksonEncoder())
                .target(DropboxClient.class, "https://api.dropboxapi.com");
    }

    @Bean
    public GoogleClient googleClientConfig(){
        return Feign.builder()
                .client(new OkHttpClient())
                .logger(new Slf4jLogger(GoogleClient.class))
                .logLevel(Logger.Level.FULL)
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder())
                .target(GoogleClient.class, "https://www.googleapis.com/drive/v3");
    }

    @Bean
    public YandexClient yandexClientConfig(){
        return Feign.builder()
                .client(new OkHttpClient())
                .logger(new Slf4jLogger(YandexClient.class))
                .logLevel(Logger.Level.FULL)
                .decoder(new JacksonDecoder())
                .encoder(new JacksonEncoder())
                .target(YandexClient.class, "https://cloud-api.yandex.net/v1");
    }
}
