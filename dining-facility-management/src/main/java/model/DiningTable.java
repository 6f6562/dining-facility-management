package model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dining_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DiningTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    @EqualsAndHashCode.Include
    private int id;
    @Column(name = "table_number", nullable = false)

    private int tableNumber;
    @Column(name = "status", nullable = false)
    private String status = "Available";

    @Column(name = "seating_capacity", nullable = false)
    private int seatingCapacity;

    @Column(name = "location")
    private String location;

    @OneToMany(mappedBy = "diningTable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderHeader> orderHeaders = new ArrayList<>();

    public DiningTable(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return ""+ tableNumber;
    }
}
