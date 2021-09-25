package space.artway.artwaystorage.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.users.SpaceUsage;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import com.yandex.disk.rest.ProgressListener;
import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.DiskInfo;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.ResourceList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaystorage.model.Content;
import space.artway.artwaystorage.model.StorageType;
import space.artway.artwaystorage.repository.ContentRepository;
import space.artway.artwaystorage.repository.TokenRepository;
import space.artway.artwaystorage.service.dto.FileDto;
import space.artway.artwaystorage.service.dto.dropbox.DropboxAccessToken;
import space.artway.artwaystorage.service.dto.google.GoogleAccessToken;
import space.artway.artwaystorage.service.dto.yandex.YandexAccessToken;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
public class SaveContentService {
    private final ContentRepository<String> contentRepository;
    private final AccessTokenInjector accessTokenInjector;
    private String yandexToken;
    private String googleToken;
    private String dropboxToken;

    public SaveContentService(TokenRepository tokenRepository, ContentRepository<String> contentRepository) {
        this.accessTokenInjector = new AccessTokenInjector(tokenRepository);
        this.contentRepository = contentRepository;
    }

    @SneakyThrows
    public FileDto saveContent(MultipartFile file) {
        dropboxToken = accessTokenInjector.getToken(StorageType.DROPBOX, DropboxAccessToken.class);
        googleToken = accessTokenInjector.getToken(StorageType.GOOGLE_DRIVE, GoogleAccessToken.class);
        yandexToken = accessTokenInjector.getToken(StorageType.YANDEX_DISK, YandexAccessToken.class);
        StorageType storageType = chooseStorage(file.getSize());
        FileDto fileDto;
        switch (storageType) {
            case DROPBOX://todo
               fileDto = saveDropbox(file, DropboxUtils.getAcceptor(dropboxToken));
                break;
            case GOOGLE_DRIVE:
               fileDto = saveGoogleDrive(file, GoogleDriveUtils.getAcceptor(googleToken));
                break;
            case YANDEX_DISK://todo
               fileDto = saveOnYandexDisk(file, YandexDiskUtils.getAcceptor(yandexToken));
                break;
            default: throw new UnsupportedOperationException();
        }
        return fileDto;
    }

    private StorageType chooseStorage(Long fileSize) throws ServerIOException, IOException {
        long yandexFreeSpace = 0;
        long googleFreeSpace = 0;
        long dropboxFreeSpace = 0;
        if (StringUtils.isNotEmpty(yandexToken)) {
            yandexFreeSpace = getYandexFreeSpace(YandexDiskUtils.getAcceptor(yandexToken));
        }
        if (StringUtils.isNotEmpty(googleToken)) {
            googleFreeSpace = getGoogleFreeSpace(GoogleDriveUtils.getAcceptor(googleToken));
        }
        if (StringUtils.isNotEmpty(dropboxToken)) {
            dropboxFreeSpace = getDropboxFreeSpace(DropboxUtils.getAcceptor(dropboxToken));
        }

        if (fileSize < dropboxFreeSpace && dropboxFreeSpace > googleFreeSpace && dropboxFreeSpace > yandexFreeSpace) {
            return StorageType.DROPBOX;
        }

        if (fileSize < googleFreeSpace && googleFreeSpace > dropboxFreeSpace && googleFreeSpace > yandexFreeSpace) {
            return StorageType.GOOGLE_DRIVE;
        }

        if (fileSize < yandexFreeSpace && yandexFreeSpace > dropboxFreeSpace && yandexFreeSpace > googleFreeSpace) {
            return StorageType.YANDEX_DISK;
        }
        log.error("No free space on storages");
        return StorageType.ERROR;
    }

    @SneakyThrows
    private FileDto saveGoogleDrive(MultipartFile file, Drive drive) {
        AbstractInputStreamContent content = new InputStreamContent(file.getContentType(), file.getInputStream());
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(file.getName());
        com.google.api.services.drive.model.File uploadedFile = drive.files()
                .create(fileMetadata, content)
                .setFields("id, webContentLink, webViewLink, parents")
                .execute();

        Content savedFile = new Content();
        savedFile.setStorageType(StorageType.GOOGLE_DRIVE);
        savedFile.setFileId(uploadedFile.getId());
        savedFile.setFilename(uploadedFile.getOriginalFilename());

        FileDto fileDto = new FileDto(UUID.randomUUID().toString(), savedFile.getFilename());

        contentRepository.save(fileDto.getId(), savedFile);
        return fileDto;
    }

    private FileDto saveDropbox(MultipartFile file, DbxClientV2 clientV2) {
        try (InputStream in = file.getInputStream()) {
            FileMetadata fileMetadata = clientV2.files().upload("/" + file.getName()).uploadAndFinish(in);
            Content savedContent = new Content();
            savedContent.setPath(fileMetadata.getPathDisplay());
            savedContent.setFilename(fileMetadata.getName());
            savedContent.setStorageType(StorageType.DROPBOX);
            savedContent.setFileId(fileMetadata.getId());

            FileDto fileDto = new FileDto(UUID.randomUUID().toString(), savedContent.getFilename());

            contentRepository.save(fileDto.getId(), savedContent);
            return fileDto;
        } catch (IOException | DbxException e) {
            log.error(e.getMessage(), e.fillInStackTrace());
            throw new RuntimeException();
        }
    }

    @SneakyThrows
    private FileDto saveOnYandexDisk(MultipartFile file, RestClient restClient) {
        Link uploadLink = restClient.getUploadLink("disk:/" + file.getName(), true);
        restClient.uploadFile(uploadLink, false, multipartToFile(file, file.getName()), new ProgressListener() {
            @Override
            public void updateProgress(long loaded, long total) {

            }

            @Override
            public boolean hasCancelled() {
                return false;
            }
        });

        ResourceList lastUploadedResources = restClient.getLastUploadedResources(new ResourcesArgs.Builder()
                        .setFields("name, items.path")
                .setLimit(1)
                .build());
        Content savedContent = new Content();
        savedContent.setStorageType(StorageType.YANDEX_DISK);
        savedContent.setFilename(lastUploadedResources.getItems().get(0).getName());
        savedContent.setPath(lastUploadedResources.getPath());

        FileDto fileDto = new FileDto(UUID.randomUUID().toString(), savedContent.getFilename());
        contentRepository.save(fileDto.getId(), savedContent);
        return fileDto;
    }

    @SneakyThrows
    private long getDropboxFreeSpace(DbxClientV2 clientV2) {
        SpaceUsage spaceInfo = clientV2.users().getSpaceUsage();
        return spaceInfo.getAllocation().getIndividualValue().getAllocated() - spaceInfo.getUsed();
    }

    @SneakyThrows
    private long getGoogleFreeSpace(Drive drive) {
        About spaceInfo = drive.about().get().setFields("storageQuota").execute();
        return spaceInfo.getStorageQuota().getLimit() - spaceInfo.getStorageQuota().getUsageInDrive();
    }

    private Long getYandexFreeSpace(RestClient restClient) throws ServerIOException, IOException {
        DiskInfo spaceInfo = restClient.getDiskInfo();
        return spaceInfo.getTotalSpace() - spaceInfo.getUsedSpace();
    }

    private File multipartToFile(MultipartFile multipart, String fileName) throws IllegalStateException, IOException {
        File convFile = new File(fileName);
        multipart.transferTo(convFile);
        return convFile;
    }
}
