package ac.su.inclassspringsecurity.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "order_product")
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 주문 가격 및 수량 저장
    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int totalPrice;

    // 등록 및 수정 시간
    // 추적이 중요한 Column 이기에 추가
    @Column(nullable = false)
    private String createdAt;

    @Column(nullable = false)
    private String updatedAt;

    @Override
    public String toString() {
        return "OrderProduct{" +
                "id=" + id +
//                (order != null ? ", order=" + order.getId() : "") +
                "order =" + order.getId() +
                ", product=" + product +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
