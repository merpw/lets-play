package pw.mer.letsplay.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pw.mer.letsplay.model.Product;
import pw.mer.letsplay.repository.ProductRepo;

import java.util.List;

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

    public static class ProductAddRequest {
        public String name;
        public String description;
        public double price;
    }

    @PostMapping("/add")
    public Product add(@RequestBody ProductAddRequest request, Authentication authentication) {
        var product = new Product(request.name, request.description, request.price, authentication.getName());
        productRepo.save(product);
        return product;
    }
}
