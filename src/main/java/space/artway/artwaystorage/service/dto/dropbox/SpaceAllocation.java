package space.artway.artwaystorage.service.dto.dropbox;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceAllocation {
    @JsonProperty(value = "allocated")
    private Long allocated;

}
