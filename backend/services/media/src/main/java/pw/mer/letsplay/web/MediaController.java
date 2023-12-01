package pw.mer.letsplay.web;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pw.mer.letsplay.storage.StorageService;

import java.io.IOException;
import java.util.regex.Pattern;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/media")
public class MediaController {
    StorageService storageService;

    @Autowired
    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> serveMedia(@PathVariable String id, HttpServletResponse response) {
        var resource = storageService.load(id);
        if (resource == null)
            throw new ResponseStatusException(NOT_FOUND);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    static final Pattern contentTypePattern = Pattern.compile("^image/(jpeg|png|gif|webp)$");
    static final String WRONG_CONTENT_TYPE_MESSAGE = "The uploaded image has invalid type, only JPEG, PNG, GIF and WebP are supported";

    static final int MAX_FILE_SIZE = 2 * 1024 * 1024; // 2 MB
    static final String TOO_BIG_FILE_MESSAGE = "The uploaded image is too big, maximum size is 2 MB";

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {

        String contentType = file.getContentType();

        if (contentType == null || !contentTypePattern.matcher(contentType).matches()) {
            throw new ResponseStatusException(BAD_REQUEST, WRONG_CONTENT_TYPE_MESSAGE);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(BAD_REQUEST, TOO_BIG_FILE_MESSAGE);
        }

        String id = storageService.save(file);

        response.setStatus(SC_CREATED);
        return id;
    }
}
