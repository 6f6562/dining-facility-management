package model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "dish_price_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DishPriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_history_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish; // Mapping back to Dish

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "price", nullable = false)
    private double price;
}
