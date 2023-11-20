package pw.mer.letsplay.web;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/media")
public class MediaController {
    @GetMapping
    public String index() {
        return "Hello from media service!";
    }
}
