package pw.mer.letsplay.web;

import jakarta.annotation.Nullable;
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
                new ResponseStatusException(NOT_FOUND, "Product not found"));
    }

    public static class AddProductRequest {
        public String name;
        public String description;
        public Double price;
    }

    @PostMapping("/add")
    public String add(@RequestBody AddProductRequest request, Authentication authentication) {
        var product = new Product(request.name, request.description, request.price, authentication.getName());
        productRepo.save(product);
        return product.getId();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id, Authentication authentication) {
        var product = productRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));

        if (!product.getUserId().equals(authentication.getName())) {
            throw new ResponseStatusException(FORBIDDEN, "You're not an owner of this product");
        }

        productRepo.deleteById(id);
    }

    public static class UpdateProductRequest {
        @Nullable
        public String name;
        @Nullable
        public String description;
        @Nullable
        public Double price;
    }

    @PutMapping("/{id}")
    public void updateById(@PathVariable String id, @RequestBody UpdateProductRequest request, Authentication authentication) {

        var product = productRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, "Product not found"));

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
