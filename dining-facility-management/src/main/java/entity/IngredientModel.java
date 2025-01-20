package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ingredient_model")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_model_id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "ingredientModel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recipe> recipes; // Liên kết với Recipe

    @OneToMany(mappedBy = "ingredientModel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingredient> ingredients = new ArrayList<>(); // Liên kết với Ingredient
}
