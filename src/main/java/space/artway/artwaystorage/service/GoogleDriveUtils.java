package space.artway.artwaystorage.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GoogleDriveUtils {

    @SneakyThrows
    public Drive getAcceptor(String token) {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = new GoogleCredential().setAccessToken(token);
        return new Drive.Builder(HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName("Artway.Space")
                .build();
    }
}
