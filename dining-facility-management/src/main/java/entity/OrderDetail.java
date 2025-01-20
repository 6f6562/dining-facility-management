package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {

    private int id;

    private int orderQty;

    private double price;

    private LocalDateTime orderDate;

    private LocalDateTime deliveryTime;

    private double subTotal;

    private String specialInstructions;
}
