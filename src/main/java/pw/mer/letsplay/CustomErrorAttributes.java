package pw.mer.letsplay;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;


/**
 * Adds a message field to the error response. If the status is 500, the message will be "Internal server error".
 */
public class CustomErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

        var message = errorAttributes.get("status").equals(HTTP_INTERNAL_ERROR) ?
                "Internal server error" :
                getMessage(webRequest, getError(webRequest));

        errorAttributes.put("message", message);

        return errorAttributes;
    }
}
