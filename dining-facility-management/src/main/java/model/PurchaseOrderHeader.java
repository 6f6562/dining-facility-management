package model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_order_header")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PurchaseOrderHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_order_id")
    @EqualsAndHashCode.Include
    private int id;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "ship_date")
    private LocalDateTime shipDate;

    @Column(name = "sub_total", nullable = false)
    private double subTotal;

    @Column(name = "status", nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor; // Liên kết với Vendor

    @OneToMany(mappedBy = "purchaseOrderHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>(); // Liên kết với PurchaseOrderDetail
    @Override
    public String toString() {
        return ""+ id;
    }
}
