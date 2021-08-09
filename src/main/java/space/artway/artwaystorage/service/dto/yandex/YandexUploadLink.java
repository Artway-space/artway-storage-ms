package space.artway.artwaystorage.service.dto.yandex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YandexUploadLink {
    private String href;
    private String method;

}
