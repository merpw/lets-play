package pw.mer.letsplay.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pw.mer.letsplay.model.Product;
import pw.mer.letsplay.repository.ProductRepo;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepo productRepo;

    private static final String NOT_FOUND_MESSAGE = "Product not found";

    private static final String NOT_OWNER_MESSAGE = "You're not an owner of this product";

    @Autowired
    public ProductController(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @GetMapping
    public List<Product> index() {
        return productRepo.findAll();
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "Authentication", scopes = {"products:admin", "products:write"})
    public Product getById(@PathVariable String id) {
        return productRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE));
    }

    @Setter
    public static class AddProductRequest {
        @JsonProperty("name")
        @NotBlank(message = "Name is mandatory")
        @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters long")
        private String name;

        @JsonProperty("description")
        @NotBlank(message = "Description is mandatory")
        @Size(min = 3, max = 1000, message = "Description must be between 3 and 1000 characters long")
        private String description;

        @JsonProperty("price")
        @NotNull(message = "Price is mandatory")
        @PositiveOrZero(message = "Price can not be negative")
        private Double price;

        @JsonProperty("images")
        private List<String> images = List.of();

        @JsonProperty("quantity")
        @NotNull(message = "Quantity is mandatory")
        @PositiveOrZero(message = "Quantity can not be negative")
        private Integer quantity;
    }


    @PostMapping("/add")
    @SecurityRequirement(name = "Authentication", scopes = {"products:admin", "products:write"})
    public String add(@Valid @RequestBody AddProductRequest request, Authentication authentication) {
        var product = new Product(request.name, request.description, request.price, authentication.getName(), request.images, request.quantity);
        productRepo.save(product);
        return product.getId();
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Authentication", scopes = {"products:admin", "products:write"})
    public void deleteById(@PathVariable String id, Authentication authentication) {
        getProductIfAdminOrOwner(id, authentication); // throws exception if not admin or owner

        productRepo.deleteById(id);
    }

    @Setter
    public static class UpdateProductRequest {
        @JsonProperty("name")
        private Optional
                <@Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters long")
                        String> name = Optional.empty();

        @JsonProperty("description")
        private Optional
                <@Size(min = 3, max = 1000, message = "Description must be between 3 and 1000 characters long")
                        String> description = Optional.empty();

        @JsonProperty("price")
        private Optional
                <@Min(value = 0, message = "Price must be greater than 0")
                        Double> price = Optional.empty();

        @JsonProperty("images")
        private Optional<List<String>> images = Optional.empty();

        @JsonProperty("quantity")
        private Optional
                <@PositiveOrZero(message = "Quantity can not be negative")
                        Integer> quantity = Optional.empty();
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "Authentication", scopes = {"products:admin", "products:write"})
    public void updateById(@PathVariable String id, @Valid @RequestBody UpdateProductRequest request, Authentication authentication) {
        var product = getProductIfAdminOrOwner(id, authentication);

        request.name.ifPresent(product::setName);
        request.description.ifPresent(product::setDescription);
        request.price.ifPresent(product::setPrice);
        request.images.ifPresent(product::setImages);
        request.quantity.ifPresent(product::setQuantity);

        productRepo.save(product);

        productRepo.save(product);
    }

    Product getProductIfAdminOrOwner(String id, Authentication authentication) throws ResponseStatusException {
        var product = productRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE));

        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("SCOPE_products:admin"));
        boolean isOwner = product.getUserId().equals(authentication.getName());

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(FORBIDDEN, NOT_OWNER_MESSAGE);
        }

        return product;
    }
}
