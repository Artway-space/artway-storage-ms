package space.artway.artwaystorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaystorage.service.SaveContentService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ContentController {
    private final SaveContentService saveContentService;

    @PutMapping
    public ResponseEntity<?> saveContent(MultipartFile file) {
        saveContentService.saveContent(file);

       // return ResponseEntity.ok();
        return null;
    }
}
