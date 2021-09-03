package space.artway.artwaystorage.service;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DropboxUtils {

    public DbxClientV2 getAcceptor(String token) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("Artway.Space/alpha").build();
        return new DbxClientV2(config, token);
    }
}
