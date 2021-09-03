package space.artway.artwaystorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import space.artway.artwaystorage.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class StoragesOAuthController {

    private final AuthService authService;

    @GetMapping("/yandex")
    public void getYandexCode(@RequestParam String code){
       authService.getYandexToken(code);
    }

    @GetMapping("/dropbox")
    public void getDropboxCode(@RequestParam String code){
       authService.getDropboxToken(code);
    }

    @GetMapping("/google")
    public void getGoogleCode(@RequestParam String code){
        authService.getGoogleToken(code);
    }
}
