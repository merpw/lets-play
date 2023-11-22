package pw.mer.letsplay.storage;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface StorageService {
    void init();

    String save(MultipartFile file) throws IOException;

    Resource load(String filename);
}