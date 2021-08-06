package space.artway.artwaystorage.service.dto.dropbox;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DropboxAbout {
    @JsonProperty(value = "used")
    private Long used;

    @JsonProperty(value = "allocation")
    private SpaceAllocation spaceAllocation;
}
