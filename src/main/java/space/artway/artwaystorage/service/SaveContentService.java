package space.artway.artwaystorage.service;

import feign.Feign;
import feign.Response;
import feign.form.spring.SpringFormEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaystorage.client.DropboxClient;
import space.artway.artwaystorage.client.GoogleClient;
import space.artway.artwaystorage.client.YandexClient;
import space.artway.artwaystorage.service.dto.dropbox.DropboxAbout;
import space.artway.artwaystorage.service.dto.google.GoogleAboutDrive;
import space.artway.artwaystorage.service.dto.yandex.YandexAboutDisk;
import space.artway.artwaystorage.service.dto.yandex.YandexUploadLink;

@Service
@RequiredArgsConstructor
public class SaveContentService {

    private final DropboxClient dropboxClient;
    private final GoogleClient googleClient;
    private final YandexClient yandexClient;


    public void saveContent(MultipartFile file) {
        StorageType storageType = chooseStorage(file.getSize());
        switch (storageType) {
            case DROPBOX://todo
                break;
            case GOOGLE_DRIVE://todo
                break;
            case YANDEX_DISK://todo
                saveOnYandexDisk(file);
                break;
            default: //todo
        }
    }

    private void saveOnYandexDisk(MultipartFile file) {
        String token = "";
        YandexUploadLink uploadLink = yandexClient.getUploadLink(token);
        if(uploadLink.getHref()!=null){
            YandexClient uploadResource  = Feign.builder().encoder(new SpringFormEncoder())
                    .target(YandexClient.class, uploadLink.getHref());
            Response response = uploadResource.uploadFile(file);
        }
    }

    private StorageType chooseStorage(Long fileSize) {
        long yandexFreeSpace = getYandexFreeSpace();
        long googleFreeSpace = getGoogleFreeSpace();
        long dropboxFreeSpace = getDropboxFreeSpace();

        if (fileSize < dropboxFreeSpace && dropboxFreeSpace > googleFreeSpace && dropboxFreeSpace > yandexFreeSpace) {
            return StorageType.DROPBOX;
        }

        if (fileSize < googleFreeSpace && googleFreeSpace > dropboxFreeSpace && googleFreeSpace > yandexFreeSpace) {
            return StorageType.GOOGLE_DRIVE;
        }

        if (fileSize < yandexFreeSpace && yandexFreeSpace > dropboxFreeSpace && yandexFreeSpace > googleFreeSpace) {
            return StorageType.YANDEX_DISK;
        }
        return StorageType.ERROR;
    }

    private long getDropboxFreeSpace() {
        String token = "";
        DropboxAbout spaceInfo = dropboxClient.getSpaceInfo("null", token);
        return spaceInfo.getSpaceAllocation().getAllocated() - spaceInfo.getUsed();
    }

    private long getGoogleFreeSpace() {
        String token = "";
        String clientId = "";
        GoogleAboutDrive spaceInfo = googleClient.getSpaceInfo(token, clientId);
        return Long.parseLong(spaceInfo.getStorageQuota().getLimit()) - Long.parseLong(spaceInfo.getStorageQuota().getUsageInDrive());
    }

    private Long getYandexFreeSpace() {
        String token = "";
        YandexAboutDisk spaceInfo = yandexClient.getSpaceInfo( token);
        return spaceInfo.getTotalSpace() - spaceInfo.getUsedSpace();
    }

}
