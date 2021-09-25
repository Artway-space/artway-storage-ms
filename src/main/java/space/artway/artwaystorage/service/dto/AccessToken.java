package space.artway.artwaystorage.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessToken implements Serializable {
    @JsonProperty("access_token")
    protected String accessToken;
}
