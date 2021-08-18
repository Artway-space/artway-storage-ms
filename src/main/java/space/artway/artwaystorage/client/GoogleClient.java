package space.artway.artwaystorage.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaystorage.service.dto.google.GoogleAboutDrive;
import space.artway.artwaystorage.service.dto.google.GoogleFile;

@Component
public interface GoogleClient {

    @RequestLine("GET /about?fields=storageQuota,user&key={client_id}")
    @Headers({"Content-Type: application/json", "Accept: application/json", "Authorization: Bearer {token}"})
    GoogleAboutDrive getSpaceInfo(@Param("token") String token, @Param("client_id") String clientId );

    @RequestLine("POST /upload/drive/v3/files?uploadType=multipart")
    @Headers({"Content-Type: application/json", "Authorization: Bearer {token}"})
    GoogleFile uploadFile(@Param("token") String token, @Param("file") MultipartFile file);

}
