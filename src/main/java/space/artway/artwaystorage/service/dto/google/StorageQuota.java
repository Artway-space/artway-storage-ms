package space.artway.artwaystorage.service.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageQuota {
    @JsonProperty(value = "limit")
    @JsonPropertyDescription("The usage limit, if applicable")
    private String limit;

    @JsonProperty(value = "usage")
    @JsonPropertyDescription("The total usage across all services")
    private String usage;

    @JsonProperty(value = "usageInDrive")
    @JsonPropertyDescription("The usage by all files in Google Drive")
    private String usageInDrive;

    @JsonProperty(value = "usageInDriveTrash")
    @JsonPropertyDescription("The usage by trashed files in Google Drive")
    private String usageInDriveTrash;
}
