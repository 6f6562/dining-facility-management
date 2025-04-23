package model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recipe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    @EqualsAndHashCode.Include
    private int id;

    @ManyToOne
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish; // Liên kết với Dish

    @ManyToOne
    @JoinColumn(name = "ingredient_model_id", nullable = false)
    private IngredientModel ingredientModel; // Liên kết với IngredientModel

    @Column(name = "required_quantity", nullable = false)
    private double requiredQuantity; // Lượng nguyên liệu cần thiết
    @Override
    public String toString() {
        return ""+ id;
    }
}
