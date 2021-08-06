package space.artway.artwaystorage.service.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriveUser {
    @JsonProperty(value = "displayName")
    @JsonPropertyDescription("A plain text displayable name for this user")
    private String displayName;

    @JsonProperty(value = "photoLink")
    @JsonPropertyDescription("A link to the user's profile photo, if available.")
    private String photoLink;

    @JsonProperty(value = "me")
    @JsonPropertyDescription("Whether this user is the requesting user")
    private Boolean me;

    @JsonProperty(value = "permissionId")
    @JsonPropertyDescription("The email address of the user")
    private String permissionId;

    @JsonProperty(value = "emailAddress")
    @JsonPropertyDescription("The email address of the user")
    private String emailAddress;
}
