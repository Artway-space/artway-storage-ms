package space.artway.artwaystorage.service.dto.google;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonClassDescription("Google OAuth token")
public class GoogleAccessToken implements Serializable {
    @JsonProperty(value = "access_token")
    @JsonPropertyDescription("The token that your application sends to authorize a Google API request")
    private String accessToken;

    @JsonProperty(value = "expires_in")
    @JsonPropertyDescription("The remaining lifetime of the access token in seconds.")
    private Long expiresIn;

    @JsonProperty(value = "token_type")
    @JsonPropertyDescription("The type of token returned. At this time, this field's value is always set to Bearer")
    private String tokenType;

    @JsonProperty(value = "scope")
    @JsonPropertyDescription("The scopes of access granted by the access_token expressed as a list of space-delimited, case-sensitive strings")
    private String scope;

    @JsonProperty(value = "refresh_token")
    @JsonPropertyDescription("A token that you can use to obtain a new access token")
    private String refreshToken;

    private LocalDateTime createdDate;

    public GoogleAccessToken() {
        this.createdDate = LocalDateTime.now();
    }
}
