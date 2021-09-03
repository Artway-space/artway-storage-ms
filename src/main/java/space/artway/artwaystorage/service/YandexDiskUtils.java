package space.artway.artwaystorage.service;

import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.RestClient;
import lombok.experimental.UtilityClass;

@UtilityClass
public class YandexDiskUtils {

    public RestClient getAcceptor(String token) {
        return new RestClient(new Credentials("", token));

    }

}
