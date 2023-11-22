package pw.mer.letsplay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pw.mer.letsplay.storage.LocalDirStorageService;
import pw.mer.letsplay.storage.StorageService;

@Configuration
public class StorageConfig {
    @Bean(initMethod = "init")
    public StorageService storageService() {
        return new LocalDirStorageService();
    }
}
