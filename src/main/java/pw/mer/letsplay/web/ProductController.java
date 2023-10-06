package pw.mer.letsplay.web;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pw.mer.letsplay.model.Product;
import pw.mer.letsplay.repository.ProductRepo;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepo productRepo;

    private static final String NOT_FOUND_MESSAGE = "Product not found";

    @Autowired
    public ProductController(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @GetMapping
    public List<Product> index() {
        return productRepo.findAll();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable String id) {
        return productRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE));
    }

    @Getter
    public static class AddProductRequest {
        @NotBlank(message = "Name is mandatory")
        @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters long")
        String name;

        @NotBlank(message = "Description is mandatory")
        @Size(min = 3, max = 1000, message = "Description must be between 3 and 1000 characters long")
        String description;

        @NotNull(message = "Price is mandatory")
        @PositiveOrZero(message = "Price can not be negative")
        Double price;
    }

    @PostMapping("/add")
    public String add(@Valid @RequestBody AddProductRequest request, Authentication authentication) {
        var product = new Product(request.name, request.description, request.price, authentication.getName());
        productRepo.save(product);
        return product.getId();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id, Authentication authentication) {
        var product = productRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE));

        if (!product.getUserId().equals(authentication.getName())) {
            throw new ResponseStatusException(FORBIDDEN, "You're not an owner of this product");
        }

        productRepo.deleteById(id);
    }

    @Getter
    public static class UpdateProductRequest {
        @Nullable
        @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters long")
        String name;

        @Nullable
        @Size(min = 3, max = 1000, message = "Description must be between 3 and 1000 characters long")
        String description;

        @Nullable
        @Min(value = 0, message = "Price must be greater than 0")
        Double price;
    }

    @PutMapping("/{id}")
    public void updateById(@PathVariable String id, @Valid @RequestBody UpdateProductRequest request, Authentication authentication) {

        var product = productRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE));

        if (!product.getUserId().equals(authentication.getName())) {
            throw new ResponseStatusException(FORBIDDEN, "You're not an owner of this product");
        }

        if (request.name != null) {
            product.setName(request.name);
        }

        if (request.description != null) {
            product.setDescription(request.description);
        }

        if (request.price != null) {
            product.setPrice(request.price);
        }

        productRepo.save(product);
    }
}
