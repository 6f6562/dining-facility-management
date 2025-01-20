package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@jakarta.persistence.Table(name = "dining_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiningTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    private int id;
    @Column(name = "table_number", nullable = false)
    private int tableNumber;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "seating_capacity", nullable = false)
    private int seatingCapacity;

    @OneToMany(mappedBy = "diningTable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderHeader> orderHeaders = new ArrayList<>();

    public DiningTable(int id) {
        this.id = id;
    }
}
