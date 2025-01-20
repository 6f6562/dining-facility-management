package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientBatch {

    private int id;

    private double stockQuantity;

    private LocalDate receivedDate;

    private LocalDate expiryDate;
}
