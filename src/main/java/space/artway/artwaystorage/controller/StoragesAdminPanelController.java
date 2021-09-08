package space.artway.artwaystorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import space.artway.artwaystorage.model.StorageType;
import space.artway.artwaystorage.service.AuthService;
import space.artway.artwaystorage.service.dto.dropbox.DropboxAccessToken;
import space.artway.artwaystorage.service.dto.google.GoogleAccessToken;
import space.artway.artwaystorage.service.dto.yandex.YandexAccessToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class StoragesAdminPanelController {
    private final AuthService authService;

    @Value("${artway.storage.cloud.yandex-disk.client_id}")
    private String yandexClientId;
    @Value("${artway.storage.cloud.dropbox.app_key}")
    private String dbxClientId;
    @Value("${artway.storage.cloud.google-drive.client_id}")
    private String googleClientId;
    @Value("${artway.storage.cloud.dropbox.redirect_uri}")
    private String dbxRedirectUri;
    @Value("${server.host}")
    private String host;
    @Value("${server.port}")
    private int port;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd MMMM yyyy");

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("yndxClientId", yandexClientId);
        model.addAttribute("dbxClientId", dbxClientId);
        model.addAttribute("dbxRedirectUri", dbxRedirectUri);
        model.addAttribute("googleClientId", googleClientId);
        model.addAttribute("hostAndPort", host + ":" + port);

        showYandexInfo(model);
        showDropboxInfo(model);
        showGoogleInfo(model);

        return "admin";
    }

    @GetMapping("revoke-dbx-token")
    public String revokeDropboxToken() {
        authService.revokeDropboxToken();
        return "redirect:/admin";
    }

    @GetMapping("/revoke-google-token")
    public String revokeGoogleToken() {
        authService.revokeGoogleToken();
        return "redirect:/admin";
    }

    private void showGoogleInfo(Model model) {
        final GoogleAccessToken googleTokenInfo = (GoogleAccessToken) authService.getByStorageType(StorageType.GOOGLE_DRIVE);
        if (googleTokenInfo == null) {
            model.addAttribute("hasGoogleToken", false);
            return;
        }

        final LocalDateTime expiresGoogleDate = googleTokenInfo.getCreatedDate().plusSeconds(googleTokenInfo.getExpiresIn());
        if (LocalDateTime.now().isBefore(expiresGoogleDate)) {
            model.addAttribute("hasGoogleToken", true);
            model.addAttribute("googleTokenExpiresDate", expiresGoogleDate.format(formatter));
            return;
        }
        model.addAttribute("hasGoogleToken", false);
    }

    private void showDropboxInfo(Model model) {
        final DropboxAccessToken dbxTokenInfo = (DropboxAccessToken) authService.getByStorageType(StorageType.DROPBOX);
        if (dbxTokenInfo == null) {
            model.addAttribute("hasDbxToken", false);
            return;
        }
        final LocalDateTime expiresDbxDate = dbxTokenInfo.getCreatedDate().plusSeconds(Long.parseLong(dbxTokenInfo.getExpiresIn()));
        if (LocalDateTime.now().isBefore(expiresDbxDate)) {
            model.addAttribute("hasDbxToken", true);
            model.addAttribute("dbxTokenExpiresDate", expiresDbxDate.format(formatter));
            return;
        }
        model.addAttribute("hasDbxToken", false);
    }

    private void showYandexInfo(Model model) {
        final YandexAccessToken yndxTokenInfo = (YandexAccessToken) authService.getByStorageType(StorageType.YANDEX_DISK);
        if (yndxTokenInfo == null) {
            model.addAttribute("hasYndxToken", false);
            return;
        }
        final LocalDateTime expiresYndxDate = yndxTokenInfo.getCreatedDate().plusSeconds(yndxTokenInfo.getExpiresIn());
        if (LocalDateTime.now().isBefore(expiresYndxDate)) {
            model.addAttribute("hasYndxToken", true);
            model.addAttribute("yndxTokenExpiresDate", expiresYndxDate.format(formatter));
            return;
        }
        model.addAttribute("hasYndxToken", false);
    }
}
