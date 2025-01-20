package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recipe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish; // Liên kết với Dish

    @ManyToOne
    @JoinColumn(name = "ingredient_model_id", nullable = false)
    private IngredientModel ingredientModel; // Liên kết với IngredientModel

    @Column(name = "required_quantity", nullable = false)
    private double requiredQuantity; // Lượng nguyên liệu cần thiết
}
