package pw.mer.letsplay.web;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    private static final String ERROR_MAPPING = "/error";

    @RequestMapping(value = ERROR_MAPPING)
    public ResponseEntity<ProblemDetail> error(HttpServletResponse response) {
        HttpStatus status = HttpStatus.valueOf(response.getStatus());
        return ResponseEntity.status(status).body(ProblemDetail.forStatus(status));
    }
}