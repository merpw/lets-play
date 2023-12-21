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
import pw.mer.letsplay.model.EStatus;
import pw.mer.letsplay.model.Order;
import pw.mer.letsplay.repository.OrdersRepo;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/orders")
public class OrdersController {
    private final OrdersRepo ordersRepo;

    private static final String NOT_FOUND_MESSAGE = "Order not found";

    private static final String FORBIDDEN_MESSAGE = "You're not allowed to access this order";

    @Autowired
    public OrdersController(OrdersRepo ordersRepo) {
        this.ordersRepo = ordersRepo;
    }

    @GetMapping
    @SecurityRequirement(name = "Authentication")
    public List<Order> index(Authentication authentication) {
        if (isAdmin(authentication)) {
            return ordersRepo.findAll();
        }

        var isSeller = authentication.getAuthorities().contains(new SimpleGrantedAuthority("SCOPE_orders:write"));

        if (isSeller) {
            return ordersRepo.findAllBySeller(authentication.getName());
        }

        var buyer = authentication.getName();

        return ordersRepo.findAllByBuyer(buyer);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "Authentication")
    public Order getById(@PathVariable String id, Authentication authentication) {
        var order = ordersRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE));

        var buyer = authentication.getName();
        if (!isAdmin(authentication) &&
                !buyer.equals(order.getSeller()) &&
                !buyer.equals(order.getBuyer())
        ) {
            throw new ResponseStatusException(FORBIDDEN, FORBIDDEN_MESSAGE);
        }

        return order;
    }

    @Setter
    public static class AddOrderRequest {
        @JsonProperty("products")
        @NotEmpty(message = "Products is mandatory")
        List<String> products;

        @JsonProperty("totalPrice")
        @NotNull(message = "Total price is mandatory")
        @PositiveOrZero(message = "Total price can not be negative")
        Double totalPrice;

        @JsonProperty("seller")
        @NotEmpty(message = "Seller is mandatory")
        String seller;
    }


    @PostMapping("/add")
    @SecurityRequirement(name = "Authentication")
    public String add(@Valid @RequestBody AddOrderRequest request, Authentication authentication) {
        var buyer = authentication.getName();

        var order = new Order(buyer, request.seller, request.products, request.totalPrice);
        ordersRepo.save(order);

        return order.getId();
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Authentication", scopes = {"products:admin", "products:write"})
    public void deleteById(@PathVariable String id, Authentication authentication) {
        if (!ordersRepo.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE);
        }

        if (!isAdmin(authentication)) {
            throw new ResponseStatusException(FORBIDDEN, "Only admin can delete orders");
        }

        ordersRepo.deleteById(id);
    }

    @Setter
    public static class UpdateOrderRequest {
        @JsonProperty("status")
        private Optional<String> status = Optional.empty();
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "Authentication")
    public void updateById(@PathVariable String id, @Valid @RequestBody OrdersController.UpdateOrderRequest request, Authentication authentication) {
        var order = ordersRepo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, NOT_FOUND_MESSAGE));

        var isAdmin = isAdmin(authentication);
        var isOwner = order.getSeller().equals(authentication.getName());

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(FORBIDDEN, FORBIDDEN_MESSAGE);
        }

        request.status.ifPresent(status -> {
            try {
                order.setStatus(EStatus.fromString(status));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(BAD_REQUEST, EStatus.VALIDATION_MESSAGE);
            }
        });

        ordersRepo.save(order);
    }

    boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("SCOPE_orders:admin"));
    }
}
