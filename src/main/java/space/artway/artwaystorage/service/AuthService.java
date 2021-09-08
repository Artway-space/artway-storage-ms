package space.artway.artwaystorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import space.artway.artwaystorage.client.DropboxClient;
import space.artway.artwaystorage.client.GoogleClient;
import space.artway.artwaystorage.client.YandexClient;
import space.artway.artwaystorage.model.StorageType;
import space.artway.artwaystorage.repository.TokenRepository;
import space.artway.artwaystorage.service.dto.dropbox.DropboxAccessToken;
import space.artway.artwaystorage.service.dto.google.GoogleAccessToken;
import space.artway.artwaystorage.service.dto.yandex.YandexAccessToken;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final YandexClient yandexClient;
    private final DropboxClient dropboxClient;
    private final GoogleClient googleClient;
    private final TokenRepository tokenRepository;

    @Value("${artway.storage.cloud.dropbox.app_key}")
    private String dropboxAppKey;
    @Value("${artway.storage.cloud.dropbox.app_secret}")
    private String dropboxAppSecret;

    @Value("${artway.storage.cloud.google-drive.client_id}")
    private String googleClientId;
    @Value("${artway.storage.cloud.google-drive.client_secret}")
    private String googleClientSecret;

    @Value("${artway.storage.cloud.yandex-disk.client_id}")
    private String yandexClientId;
    @Value("${artway.storage.cloud.yandex-disk.client_secret}")
    private String yandexClientSecret;


    final String AUTHORIZATION_CODE = "authorization_code";
    final String REFRESH_TOKEN = "refresh_token";

    public void getYandexToken(String code) {
        String auth = Base64.getEncoder().encodeToString((yandexClientId + ":" + yandexClientSecret).getBytes(StandardCharsets.UTF_8));
        YandexAccessToken token = yandexClient.getToken(auth, AUTHORIZATION_CODE, code);
        tokenRepository.save(StorageType.YANDEX_DISK, token);
    }

    public void refreshYandexToken() {
        YandexAccessToken oldToken = (YandexAccessToken) tokenRepository.findByKey(StorageType.YANDEX_DISK);
        if (isExpired(oldToken.getCreatedDate(), oldToken.getExpiresIn())) {
            String auth = Base64.getEncoder().encodeToString((yandexClientId + ":" + yandexClientSecret).getBytes(StandardCharsets.UTF_8));
            YandexAccessToken token = yandexClient.refreshToken(auth, REFRESH_TOKEN, oldToken.getRefreshToken());
            tokenRepository.save(StorageType.YANDEX_DISK, token);
        }
    }

    public void getDropboxToken(String code) {
        String auth = Base64.getEncoder().encodeToString((dropboxAppKey + ":" + dropboxAppSecret).getBytes(StandardCharsets.UTF_8));
        final DropboxAccessToken token = dropboxClient.getToken(auth, code, AUTHORIZATION_CODE, "http://localhost:8089/api/auth/dropbox");
        tokenRepository.save(StorageType.DROPBOX, token);
    }

    public void dropboxRefreshToken() {
        DropboxAccessToken oldToken = (DropboxAccessToken) tokenRepository.findByKey(StorageType.DROPBOX);
        if (StringUtils.isNotEmpty(oldToken.getExpiresIn()) && isExpired(oldToken.getCreatedDate(), Long.parseLong(oldToken.getExpiresIn()))) {
            String auth = Base64.getEncoder().encodeToString((dropboxAppKey + ":" + dropboxAppSecret).getBytes(StandardCharsets.UTF_8));
            final DropboxAccessToken token = dropboxClient.refreshToken(auth, oldToken.getRefreshToken(), REFRESH_TOKEN);
            if (token.getRefreshToken() == null) {
                token.setRefreshToken(oldToken.getRefreshToken());
            }
            tokenRepository.save(StorageType.DROPBOX, token);
        }
    }

    public void revokeDropboxToken(){
        DropboxAccessToken token = (DropboxAccessToken) tokenRepository.findByKey(StorageType.DROPBOX);
        dropboxClient.revokeToken(token.getAccessToken());
        tokenRepository.deleteByKey(StorageType.DROPBOX);
    }

    public void getGoogleToken(String code) {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("code", code);
        formParams.put("client_id", googleClientId);
        formParams.put("client_secret", googleClientSecret);
        formParams.put("redirect_uri", "http://localhost:8089/api/auth/google");
        formParams.put("grant_type", AUTHORIZATION_CODE);
        GoogleAccessToken token = googleClient.getToken(formParams);
        tokenRepository.save(StorageType.GOOGLE_DRIVE, token);
    }

    public void googleRefreshToken() {
        GoogleAccessToken oldToken = (GoogleAccessToken) tokenRepository.findByKey(StorageType.GOOGLE_DRIVE);
        if (isExpired(oldToken.getCreatedDate(), oldToken.getExpiresIn())) {
            Map<String, String> formParams = new HashMap<>();
            formParams.put("client_id", googleClientId);
            formParams.put("client_secret", googleClientSecret);
            formParams.put("refresh_token", oldToken.getRefreshToken());
            formParams.put("grant_type", REFRESH_TOKEN);
            GoogleAccessToken token = googleClient.getToken(formParams);
            tokenRepository.save(StorageType.GOOGLE_DRIVE, token);
        }
    }

    public void revokeGoogleToken() {
        GoogleAccessToken token = (GoogleAccessToken) tokenRepository.findByKey(StorageType.GOOGLE_DRIVE);
        googleClient.revokeToken(token.getAccessToken());
        tokenRepository.deleteByKey(StorageType.GOOGLE_DRIVE);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60) //One hour
    public void autoRefreshTokens() {
        if (tokenRepository.findByKey(StorageType.DROPBOX) != null) {
            dropboxRefreshToken();
        }
        if (tokenRepository.findByKey(StorageType.GOOGLE_DRIVE) != null) {
            googleRefreshToken();
        }
        if (tokenRepository.findByKey(StorageType.YANDEX_DISK) != null) {
            refreshYandexToken();
        }
    }

    public Object getByStorageType(StorageType storageType) {
        return tokenRepository.findByKey(storageType);
    }

    private boolean isExpired(LocalDateTime createdDate, long expiredIn) {
        return LocalDateTime.now().isBefore(createdDate.plusSeconds(expiredIn / 100));
    }
}
