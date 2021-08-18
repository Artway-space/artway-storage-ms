package space.artway.artwaystorage.service.dto.google;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleFile {
    private String kind;

    private String id;

    private String name;

    private String mimeType;

    private Boolean starred;

    private Boolean trashed;

    private String webViewLink;

    private String iconLink;

    private String createdTime;

    private String modifiedTime;

    private Boolean shared;

    private String originalFileName;

    private String fullFileExtension;

    private String size;
}
