package space.artway.artwaystorage.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.stereotype.Component;
import space.artway.artwaystorage.service.dto.google.GoogleAccessToken;

import java.util.Map;

@Component
public interface GoogleClient {
    @RequestLine("POST /token")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    GoogleAccessToken getToken(Map<String, ?> formParams);

    @RequestLine("POST /revoke?token={token}")
    @Headers({"Content-Type:application/x-www-form-urlencoded"})
    void revokeToken(@Param("token") String token);
}
