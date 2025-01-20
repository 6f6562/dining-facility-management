package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiningTable {

    private int id;

    private int tableNumber;

    private String status;

    private int seatingCapacity;

}
