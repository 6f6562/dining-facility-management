package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DishPriceHistory {

    private int id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private double price;
}
