package space.artway.artwaystorage.service.dto.yandex;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import space.artway.artwaystorage.service.dto.AccessToken;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonClassDescription("Yandex OAuth token")
public class YandexAccessToken extends AccessToken implements Serializable {
    //private static final long serialVersionUID = -4591844763186478113L;

    @JsonProperty(value = "token_type")
    @JsonPropertyDescription("Type of token. Always is bearer")
    private String tokenType;

//    @JsonProperty(value = "access_token")
//    @JsonPropertyDescription("OAuth token with selected rights")
//    private String accessToken;

    @JsonProperty(value = "expires_in")
    @JsonPropertyDescription("Life time of token in seconds")
    private Long expiresIn;

    @JsonProperty(value = "refresh_token")
    @JsonPropertyDescription("Token witch can use for refresh OAuth token")
    private String refreshToken;

    @JsonProperty(value = "error_description")
    @JsonPropertyDescription("Error description")
    private String errorDescription;

    @JsonProperty(value = "error")
    @JsonPropertyDescription("Error code")
    private String error;

    private LocalDateTime createdDate;

    public YandexAccessToken() {
        this.createdDate = LocalDateTime.now();
    }
}
