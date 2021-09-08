package space.artway.artwaystorage.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.stereotype.Component;
import space.artway.artwaystorage.service.dto.dropbox.DropboxAccessToken;

@Component
public interface DropboxClient {

    @RequestLine("POST /oauth2/token")
    @Headers({"Content-Type: application/x-www-form-urlencoded", "Authorization: Basic {encoded_string}"})
    DropboxAccessToken getToken(@Param("encoded_string") String auth,
                                @Param("code") String code,
                                @Param("grant_type") String grandType,
                                @Param("redirect_uri") String redirectUrl);

    @RequestLine("POST /oauth2/token")
    @Headers({"Content-Type: application/x-www-form-urlencoded", "Authorization: Basic {encoded_string}"})
    DropboxAccessToken refreshToken(@Param("encoded_string") String auth,
                                @Param("refresh_token") String refreshToken,
                                @Param("grant_type") String grandType);

    @RequestLine("POST /2/auth/token/revoke")
    @Headers({"Authorization: Bearer {access_token}"})
    void revokeToken(@Param("access_token") String accessToken);
}
