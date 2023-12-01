package pw.mer.letsplay.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Local directory storage service, stores files in local directory.
 */
public class LocalDirStorageService implements StorageService {
    @Value("${media.storage-dir}")
    private String storageDir;

    private File rootLocation;

    public void init() {
        rootLocation = new File(storageDir);
        if (!rootLocation.exists() && !rootLocation.mkdirs()) {
            throw new RuntimeException("Cannot create storage directory");
        }
    }

    public String save(MultipartFile file) throws IOException {
        String id = UUID.randomUUID().toString();

        var localFile = new File(rootLocation, id);

        if (!localFile.createNewFile()) {
            throw new IOException("File already exists");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, localFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        return id;
    }

    public Resource load(String filename) {
        try {
            File file = new File(rootLocation, filename);
            Resource resource = new UrlResource(file.toURI());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
