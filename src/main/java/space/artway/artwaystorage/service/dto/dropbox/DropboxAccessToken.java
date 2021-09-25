package space.artway.artwaystorage.service.dto.dropbox;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import space.artway.artwaystorage.service.dto.AccessToken;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DropboxAccessToken extends AccessToken implements Serializable {
    //private static final long serialVersionUID = -8249582491158650400L;

//    @JsonProperty("access_token")
//    private String accessToken;

    @JsonProperty("expires_in")
    private String expiresIn;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("uid")
    private String uid;

    private LocalDateTime createdDate;

    public DropboxAccessToken() {
        this.createdDate = LocalDateTime.now();
    }
}
