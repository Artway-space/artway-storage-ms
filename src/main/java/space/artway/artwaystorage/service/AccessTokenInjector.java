package space.artway.artwaystorage.service;

import lombok.RequiredArgsConstructor;
import space.artway.artwaystorage.model.StorageType;
import space.artway.artwaystorage.repository.TokenRepository;
import space.artway.artwaystorage.service.dto.AccessToken;

import java.util.Optional;

@RequiredArgsConstructor
public class AccessTokenInjector<T> {
    private final TokenRepository<StorageType, T> tokenRepository;

    public String getToken(StorageType storageType, Class<? extends AccessToken> clazz) {
        final Optional<AccessToken> token = Optional.ofNullable(clazz.cast(tokenRepository.findByKey(storageType)));
        return token.map(AccessToken::getAccessToken).orElse("");
    }
}
