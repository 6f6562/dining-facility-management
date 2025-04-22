package model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_order_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_order_detail_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrderHeader purchaseOrderHeader; // Liên kết với PurchaseOrderHeader

    @Column(name = "order_qty", nullable = false)
    private double orderQty;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(name = "line_total", nullable = false)
    private double lineTotal;

    @Column(name = "received_qty", nullable = false)
    private double receivedQty;

    @Column(name = "rejected_qty", nullable = false)
    private double rejectedQty;

    @OneToMany(mappedBy = "purchaseOrderDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IngredientBatch> ingredientBatches = new ArrayList<>(); // Liên kết với IngredientBatch
}
