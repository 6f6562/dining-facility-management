package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ingredient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "ingredient_model_id", nullable = false)
    private IngredientModel ingredientModel; // Liên kết với IngredientModel

    @Column(name = "unit_of_measure", nullable = false)
    private String unitOfMeasure;

    @Column(name = "stock_quantity", nullable = false)
    private double stockQuantity;

    @Column(name = "expiry_date", nullable = false)
    private java.time.LocalDate expiryDate;

    @Column(name = "safety_stock_level", nullable = false)
    private double safetyStockLevel;

    @Column(name = "reorder_point", nullable = false)
    private double reorderPoint;

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IngredientBatch> ingredientBatches = new ArrayList<>(); // Liên kết với IngredientBatch
}
