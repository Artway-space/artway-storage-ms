package space.artway.artwaystorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import space.artway.artwaystorage.model.StorageType;
import space.artway.artwaystorage.service.AuthService;
import space.artway.artwaystorage.service.dto.dropbox.DropboxAccessToken;
import space.artway.artwaystorage.service.dto.yandex.YandexAccessToken;

@Controller
@RequiredArgsConstructor
public class StoragesAdminPanelController {
    private final AuthService authService;

    @Value("${artway.storage.cloud.yandex-disk.client_id}")
    private String yandexClientId;
    @Value("${artway.storage.cloud.yandex-disk.client_secret}")
    private String yandexClientSecret;



    @GetMapping("/admin")
    public String index(Model model) {
        model.addAttribute("yndxClientId", yandexClientId);
        model.addAttribute("dbxClientId", yandexClientSecret);
        model.addAttribute("redirectUrl", "http://localhost:8089/api/auth/yandex");
        model.addAttribute("yndxTokenInfo", (YandexAccessToken) authService.getByStorageType(StorageType.YANDEX_DISK));
        model.addAttribute("dbxTokenInfo", (DropboxAccessToken) authService.getByStorageType(StorageType.DROPBOX));
        return "admin";
    }

//    @GetMapping("/google/get_token")
//    public String getGoogleCredentials() throws GeneralSecurityException, IOException {
//        authService.getGoogleCredentials();
//        return "forward:/admin";
//    }
}
