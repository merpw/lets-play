package pw.mer.letsplay.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Document("Product")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    @Setter
    private String name;

    @JsonProperty("description")
    @Setter
    private String description;

    @JsonProperty("price")
    @Setter
    private Double price;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("images")
    @Setter
    private List<String> images;

    @JsonProperty("quantity")
    @Setter
    private Integer quantity;

    public Product(String name, String description, Double price, String userId, List<String> images, Integer quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.userId = userId;
        this.images = images;
        this.quantity = quantity;
    }
}
