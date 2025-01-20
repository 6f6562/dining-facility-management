package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vendor {

    private int id;

    private String name;

    private String address;

    private boolean activeFlag;

    private boolean preferredVendorStatus;
}
