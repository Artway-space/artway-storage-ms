package space.artway.artwaystorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaystorage.service.SaveContentService;

@RestController
@RequiredArgsConstructor
public class ContentController {
    private final SaveContentService saveContentService;

    public ResponseEntity<?> saveContent(MultipartFile file) {
        saveContentService.saveContent(file);

       // return ResponseEntity.ok();
        return null;
    }
}
