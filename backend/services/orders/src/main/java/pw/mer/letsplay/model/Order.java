package pw.mer.letsplay.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Document("Order")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("buyer")
    private String buyer;

    @JsonProperty("seller")
    private String seller;

    @JsonProperty("products")
    private List<Product> products;

    @JsonProperty("totalPrice")
    private Double totalPrice;

    @JsonProperty("status")
    @Setter
    private EStatus status;

    @JsonProperty("createdAt")
    private Long createdAt;

    public Order(String buyer, String seller, List<Product> products, Double totalPrice) {
        this.buyer = buyer;
        this.seller = seller;
        this.products = products;
        this.totalPrice = totalPrice;
        this.status = EStatus.PENDING;

        this.createdAt = System.currentTimeMillis();
    }
}
