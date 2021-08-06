package space.artway.artwaystorage.repository;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.stereotype.Component;
import space.artway.artwaystorage.service.dto.yandex.YandexAboutDisk;

@Component
public interface YandexClient {

    @RequestLine("GET /v1/disk/")
    @Headers({"Content-Type: application/json", "Accept: application/json", "Authorization: OAuth {token}"})
    YandexAboutDisk getSpaceInfo(@Param("token") String token);
}
