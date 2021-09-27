package space.artway.artwaystorage.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.google.api.services.drive.Drive;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.Link;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import space.artway.artwaystorage.model.Content;
import space.artway.artwaystorage.model.StorageType;
import space.artway.artwaystorage.repository.ContentRepository;
import space.artway.artwaystorage.repository.TokenRepository;
import space.artway.artwaystorage.service.dto.dropbox.DropboxAccessToken;
import space.artway.artwaystorage.service.dto.google.GoogleAccessToken;
import space.artway.artwaystorage.service.dto.yandex.YandexAccessToken;

import java.io.IOException;

@Slf4j
@Service
public class DeleteService {
    private final TokenRepository tokenRepository;
    private final ContentRepository<String> contentRepository;
    private final AccessTokenInjector tokenControl;

    public DeleteService(TokenRepository tokenRepository, ContentRepository<String> contentRepository) {
        this.tokenRepository = tokenRepository;
        this.tokenControl = new AccessTokenInjector(tokenRepository);
        this.contentRepository = contentRepository;
    }

    public void deleteContent(String contentId) {
        final Content content = contentRepository.findById(contentId);
        switch (content.getStorageType()) {
            case DROPBOX:
                deleteFromDropbox(content.getPath());
                deleteFromDatabase(contentId);
                break;
            case GOOGLE_DRIVE:
                deleteFromGoogleDrive(content.getFileId());
                deleteFromDatabase(contentId);
                break;
            case YANDEX_DISK:
                deleteFromYandexDisk(content.getPath());
                deleteFromDatabase(contentId);
                break;
        }
    }

    public void deleteFromYandexDisk(String file) {
        try {
            final RestClient client = YandexDiskUtils.getAcceptor(tokenControl.getToken(StorageType.YANDEX_DISK, YandexAccessToken.class));
            final Link delete = client.delete(file, true);
            if ("done".equals(delete.getHttpStatus())) {
                log.error("File not deleted");
            }
        } catch (ServerIOException | IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    public void deleteFromDropbox(String path) {
        try {
            final DbxClientV2 client = DropboxUtils.getAcceptor(tokenControl.getToken(StorageType.DROPBOX, DropboxAccessToken.class));
            client.files().permanentlyDelete(path);
        } catch (DbxException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

    }

    public void deleteFromGoogleDrive(String fileId) {
        try {
            final Drive client = GoogleDriveUtils.getAcceptor(tokenControl.getToken(StorageType.GOOGLE_DRIVE, GoogleAccessToken.class));
            client.files().delete(fileId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFromDatabase(String contentId) {
        contentRepository.findById(contentId);
    }
}
