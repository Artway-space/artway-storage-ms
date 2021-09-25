package space.artway.artwaystorage.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Content implements Serializable {
    private StorageType storageType;
    private String path;
    private String fileId;
    private String filename;
}
