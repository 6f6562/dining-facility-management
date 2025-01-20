package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDetail {

    private int id;

    private double orderQty;

    private double unitPrice;

    private double lineTotal;

    private double receivedQty;

    private double rejectedQty;
}
