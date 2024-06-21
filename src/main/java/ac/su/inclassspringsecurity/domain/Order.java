package ac.su.inclassspringsecurity.domain;

import ac.su.inclassspringsecurity.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Getter @Setter
@Table(name = "orders") // oder 는 예약어이므로 orders 로 테이블명 설정
public class Order {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    // @OneToOne = @ManyToOne + @JoinColumn(unique = true)
//    @OneToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(nullable = false)
    @Enumerated(
//            EnumType.ORDINAL - 기본
            EnumType.STRING
    )
    private OrderStatus status;

    @Column(nullable = false)
    private String createdAt;

    @Column(nullable = false)
    private String updatedAt;

    // 인라인 중계 테이블 생성(별도 엔티티를 사용하지 않음)
//    @ManyToMany
//    @JoinTable(
//            name = "order_product",
//            joinColumns = @JoinColumn(name = "order_id"),
//            inverseJoinColumns = @JoinColumn(name = "product_id"))
//    private List<Product> products;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", user=" + user.getId() +
                ", status=" + status +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
