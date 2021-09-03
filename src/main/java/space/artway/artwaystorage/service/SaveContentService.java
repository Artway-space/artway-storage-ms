package space.artway.artwaystorage.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.users.SpaceUsage;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
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
import space.artway.artwaystorage.model.StorageType;
import space.artway.artwaystorage.repository.TokenRepository;
import space.artway.artwaystorage.service.dto.dropbox.DropboxAccessToken;
import space.artway.artwaystorage.service.dto.google.GoogleAccessToken;
import space.artway.artwaystorage.service.dto.yandex.YandexAccessToken;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
public class SaveContentService {
    private final TokenRepository tokenRepository;
    private final String yandexToken;
    private final String googleToken;
    private final String dropboxToken;

    public SaveContentService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        YandexAccessToken yandexAccessToken = (YandexAccessToken) tokenRepository.findByKey(StorageType.YANDEX_DISK);
        DropboxAccessToken dropboxAccessToken = (DropboxAccessToken) tokenRepository.findByKey(StorageType.DROPBOX);
        GoogleAccessToken googleAccessToken = (GoogleAccessToken) tokenRepository.findByKey(StorageType.GOOGLE_DRIVE);

        yandexToken = Optional.ofNullable(yandexAccessToken).orElseGet(YandexAccessToken::new).getAccessToken();
        dropboxToken = Optional.ofNullable(dropboxAccessToken).orElseGet(DropboxAccessToken::new).getAccessToken();
        googleToken = Optional.ofNullable(googleAccessToken).orElseGet(GoogleAccessToken::new).getAccessToken();
    }

    //ToDO get token not in Constructor

    @SneakyThrows
    public void saveContent(MultipartFile file) {
        StorageType storageType = chooseStorage(file.getSize());
        switch (storageType) {
            case DROPBOX://todo
                saveDropbox(file, DropboxUtils.getAcceptor(dropboxToken));
                break;
            case GOOGLE_DRIVE://todo
                saveGoogleDrive(file, GoogleDriveUtils.getAcceptor(googleToken));
                break;
            case YANDEX_DISK://todo
                saveOnYandexDisk(file, YandexDiskUtils.getAcceptor(yandexToken));
                break;
            default: //todo
        }
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
    private void saveGoogleDrive(MultipartFile file, Drive drive) {
        AbstractInputStreamContent content = new InputStreamContent(file.getContentType(), file.getInputStream());
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(file.getName());
        com.google.api.services.drive.model.File uploadedFile = drive.files()
                .create(fileMetadata, content)
                .setFields("id, webContentLink, webViewLink, parents")
                .execute();

        //ToDo return something
    }

    private void saveDropbox(MultipartFile file, DbxClientV2 clientV2) {
        try (InputStream in = file.getInputStream()) {
            FileMetadata fileMetadata = clientV2.files().upload("/" + file.getName()).uploadAndFinish(in);
            //ToDo return something
        } catch (IOException | DbxException e) {
            log.error(e.getMessage(), e.fillInStackTrace());
        }
    }

    @SneakyThrows
    private void saveOnYandexDisk(MultipartFile file, RestClient restClient) {
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
                .setLimit(1)
                .build());
        //ToDo return something
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

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        String clientSecret = "EGxd2ef11BCkCCFuHaWefZhT";
        String clientId = "135842521742-lodb1fo7r02b757qtdk5ht5cq6rmapmv.apps.googleusercontent.com";

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), clientId, clientSecret, Collections.singletonList(DriveScopes.DRIVE))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
