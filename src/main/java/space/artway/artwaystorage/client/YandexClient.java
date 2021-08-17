package space.artway.artwaystorage.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaystorage.service.dto.yandex.YandexAboutDisk;
import space.artway.artwaystorage.service.dto.yandex.YandexUploadLink;

@Component
public interface YandexClient {

    @RequestLine("GET /disk/")
    @Headers({"Content-Type: application/json", "Accept: application/json", "Authorization: OAuth {token}"})
    YandexAboutDisk getSpaceInfo(@Param("token") String token);

    @RequestLine("GET /disk/resources/upload?path=app:/")
    @Headers({"Content-Type: application/json", "Accept: application/json", "Authorization: OAuth {token}"})
    YandexUploadLink getUploadLink(@Param("token") String token);

    @RequestLine("PUT")
    Response uploadFile(@Param("file")MultipartFile file);
}
