package model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "vendor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_id")
    @EqualsAndHashCode.Include
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "active_flag", nullable = false)
    private boolean activeFlag;

    @Column(name = "preferred_vendor_status", nullable = false)
    private boolean preferredVendorStatus;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderHeader> purchaseOrderHeaders;

    @Override
    public String toString() {
        return ""+ name;
    }
}
