package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dish {

    private int id;

    private String name;

    private String description;

    private String category;

    private double unitPrice;

    private int preparationTime;

    private int calories;

}
