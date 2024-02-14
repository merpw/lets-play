package pw.mer.letsplay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    @JsonProperty("id")
    String id;

    @JsonProperty("quantity")
    Number quantity;
}
