package space.artway.artwaystorage.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaystorage.service.dto.dropbox.DropboxAbout;

@Component
public interface DropboxClient {

    @RequestLine("POST /2/users/get_space_usage")
    @Headers({"Content-Type: application/json", "Accept: application/json", "Authorization: Bearer {token}"})
    DropboxAbout getSpaceInfo(Object emptyBody, @Param("token") String token);

    @RequestLine("POST files/upload")
    @Headers({"Authorization: Bearer {token}", "Dropbox-API-Arg: {path}", "Content-Type: application/octet-stream"})
    void uploadFile(@Param("token") String token, @Param("path") String path, @Param("file") MultipartFile file);
}
