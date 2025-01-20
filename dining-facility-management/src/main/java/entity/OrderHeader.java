package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@jakarta.persistence.Table (name = "order_header")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_header_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private DiningTable diningTable;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "sub_total", nullable = false)
    private double subTotal;

    @OneToMany(mappedBy = "orderHeader", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails = new ArrayList<>();
}
