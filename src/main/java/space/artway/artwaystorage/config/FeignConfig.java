package space.artway.artwaystorage.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

//    @Bean
//    public DropboxClient dropboxClientConfig(){
//        return Feign.builder()
//                .client(new OkHttpClient())
//                .logger(new Slf4jLogger(DropboxClient.class))
//                .logLevel(Logger.Level.FULL)
//                .decoder(new JacksonDecoder())
//                .encoder(new JacksonEncoder())
//                .target(DropboxClient.class, "https://api.dropboxapi.com");
//    }
//
//    @Bean
//    public GoogleClient googleClientConfig(){
//        return Feign.builder()
//                .client(new OkHttpClient())
//                .logger(new Slf4jLogger(GoogleClient.class))
//                .logLevel(Logger.Level.FULL)
//                .decoder(new JacksonDecoder())
//                .encoder(new JacksonEncoder())
//                .target(GoogleClient.class, "https://www.googleapis.com");
//    }
//
//    @Bean
//    public YandexClient yandexClientConfig(){
//        return Feign.builder()
//                .client(new OkHttpClient())
//                .logger(new Slf4jLogger(YandexClient.class))
//                .logLevel(Logger.Level.FULL)
//                .decoder(new JacksonDecoder())
//                .encoder(new JacksonEncoder())
//                .target(YandexClient.class, "https://cloud-api.yandex.net/v1");
//    }
}
