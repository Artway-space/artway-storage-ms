package space.artway.artwaystorage.service.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleAboutDrive {
    @JsonProperty(value = "storageQuota")
    @JsonPropertyDescription("The user's storage quota limits and usage. All fields are measured in bytes")
    private StorageQuota storageQuota;

    @JsonProperty(value = "user")
    @JsonPropertyDescription("The authenticated user")
    private DriveUser user;

}
