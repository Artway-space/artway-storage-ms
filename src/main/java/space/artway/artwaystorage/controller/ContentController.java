package space.artway.artwaystorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaystorage.service.DeleteService;
import space.artway.artwaystorage.service.SaveContentService;
import space.artway.artwaystorage.service.dto.FileDto;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ContentController {
    private final SaveContentService saveContentService;
    private final DeleteService deleteService;

    @PostMapping(value = "save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileDto> saveContent(@RequestPart(value = "file") MultipartFile file) {
        return ResponseEntity.ok(saveContentService.saveContent(file));
    }

    @DeleteMapping("delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteContent(@RequestParam String fileId){
        deleteService.deleteContent(fileId);

    }
}
