package space.artway.artwaystorage.service.dto.yandex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YandexUser {
    @JsonProperty(value = "country")
    private String country;
    @JsonProperty(value = "login")
    private String login;
    @JsonProperty(value = "display_name")
    private String displayName;
    @JsonProperty(value = "uid")
    private String uid;
}
