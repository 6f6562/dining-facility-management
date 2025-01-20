package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {

    private int id;

    private String name;

    private String unitOfMeasure;

    private double stockQuantity;

    private LocalDate expiryDate;

    private double safetyStockLevel;

    private double reorderPoint;

}
