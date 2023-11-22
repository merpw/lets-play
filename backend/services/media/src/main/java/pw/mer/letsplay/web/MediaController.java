package pw.mer.letsplay.web;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pw.mer.letsplay.storage.StorageService;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.IMAGE_JPEG;

@RestController
@RequestMapping("/media")
public class MediaController {
    StorageService storageService;

    @Autowired
    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> serveMedia(@PathVariable String id) {
        var resource = storageService.load(id);
        if (resource == null)
            throw new ResponseStatusException(NOT_FOUND);

        return ResponseEntity.ok()
                // TODO: maybe use more specific content type
                .contentType(IMAGE_JPEG)
                .body(resource);
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
        String id = storageService.save(file);

        response.setStatus(SC_CREATED);
        return id;
    }
}
