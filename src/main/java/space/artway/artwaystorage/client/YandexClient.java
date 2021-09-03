package space.artway.artwaystorage.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.stereotype.Component;
import space.artway.artwaystorage.service.dto.yandex.YandexAccessToken;

@Component
public interface YandexClient {

    @RequestLine("POST token")
    @Headers({"Content-Type:application/x-www-form-urlencoded", "Authorization: Basic {encoded_string}"})
    YandexAccessToken getToken(@Param("encoded_string") String auth,
                               @Param("grant_type") String grantType,
                               @Param("code") String code);

    @RequestLine("POST token")
    @Headers({"Content-Type:application/x-www-form-urlencoded", "Authorization: Basic {encoded_string}"})
    YandexAccessToken refreshToken(@Param("encoded_string") String auth,
                                   @Param("grant_type") String grantType,
                                   @Param("refresh_token") String refreshToken);
}