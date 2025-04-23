package model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ingredient_model")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class IngredientModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_model_id")
    @EqualsAndHashCode.Include
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "ingredientModel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recipe> recipes; // Liên kết với Recipe

    @OneToMany(mappedBy = "ingredientModel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingredient> ingredients = new ArrayList<>(); // Liên kết với Ingredient

    @Override
    public String toString() {
        return ""+ name;
    }
}
