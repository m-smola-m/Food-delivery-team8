
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;
    
    @Column(nullable = false)
    private Long cartId;
    
    @Column(nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public CartItem() {
    }

    public CartItem(Long cartId, Long productId, Integer quantity, BigDecimal price) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getCartItemId() { return cartItemId; }
    public Long getCartId() { return cartId; }
    public Long getProductId() { return productId; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }

    public void setCartItemId(Long cartItemId) { this.cartItemId = cartItemId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
