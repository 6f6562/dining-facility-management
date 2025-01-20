package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderHeader {

    private int id;

    private LocalDateTime orderDate;

    private LocalDateTime shipDate;

    private double subTotal;

    private String status;
}
