package space.artway.artwaystorage.service.dto.yandex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YandexAboutDisk {
    @JsonProperty(value = "unlimited_autoupload_enabled")
    private Boolean unlimitedAutouploadEnable;

    @JsonProperty(value = "max_file_size")
    private Long maxFileSize;

    @JsonProperty(value = "total_space")
    private Long totalSpace;

    @JsonProperty(value = "trash_size")
    private Long trashSize;

    @JsonProperty(value = "is_paid")
    private Boolean isPaid;

    @JsonProperty(value = "used_space")
    private Long usedSpace;

    @JsonProperty(value = "system_folders")
    private SystemFolders systemFolders;

    @JsonProperty(value = "user")
    private YandexUser user;

    @JsonProperty(value = "revision")
    private Long revision;
}
