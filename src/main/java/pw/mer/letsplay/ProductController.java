package pw.mer.letsplay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pw.mer.letsplay.model.Product;
import pw.mer.letsplay.repository.ProductRepo;

import java.util.List;

@RestController
public class ProductController {
    private final ProductRepo productRepo;

    @Autowired
    public ProductController(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @GetMapping("/products")
    public List<Product> index() {
        return productRepo.findAll();
    }
}
