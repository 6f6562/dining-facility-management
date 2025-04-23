package model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "order_header_id", nullable = false)
    private OrderHeader orderHeader;

    @ManyToOne
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish; // Liên kết với Dish

    @Column(name = "order_qty", nullable = false)
    private int orderQty;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    @Column(name = "sub_total", nullable = false)
    private double subTotal;

    @Column(name = "special_instructions")
    private String specialInstructions;

    @Column(name = "status")
    private String status;
}
