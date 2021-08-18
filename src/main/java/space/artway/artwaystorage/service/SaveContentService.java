package space.artway.artwaystorage.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.users.SpaceUsage;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;
import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.ProgressListener;
import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.DiskInfo;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.json.ResourceList;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveContentService {
    private String yandexToken = "";
    private String googleToken = "";
    private String dropboxToken = "";

    @SneakyThrows
    public void saveContent(MultipartFile file) {
        StorageType storageType = chooseStorage(file.getSize());
        switch (storageType) {
            case DROPBOX://todo
                saveDropbox(file, getDropboxAcceptor(dropboxToken));
                break;
            case GOOGLE_DRIVE://todo
                saveGoogleDrive(file, getGoogleDriveAccepter(googleToken));
                break;
            case YANDEX_DISK://todo
                saveOnYandexDisk(file, getYandexDiskAccepter(yandexToken));
                break;
            default: //todo
        }
    }

    private StorageType chooseStorage(Long fileSize) throws ServerIOException, IOException {
        long yandexFreeSpace = getYandexFreeSpace(getYandexDiskAccepter(yandexToken));
        long googleFreeSpace = getGoogleFreeSpace(getGoogleDriveAccepter(googleToken));
        long dropboxFreeSpace = getDropboxFreeSpace(getDropboxAcceptor(dropboxToken));

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

    //ToDo replace lower code to Utility classes for each cloud storages

    @SneakyThrows
    private Drive getGoogleDriveAccepter(String token) {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = new GoogleCredential().setAccessToken(token);
        return new Drive.Builder(HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName("Artway.Space")
                .build();
    }

    private RestClient getYandexDiskAccepter(String token) {
        return new RestClient(new Credentials("", token));
    }

    private DbxClientV2 getDropboxAcceptor(String token) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("Artway.Space/alpha").build();
        return new DbxClientV2(config, token);
    }

}
