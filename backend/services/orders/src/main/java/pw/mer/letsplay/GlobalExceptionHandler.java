package pw.mer.letsplay;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import pw.mer.shared.SharedGlobalExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends SharedGlobalExceptionHandler {
}