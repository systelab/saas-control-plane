package com.systelab.modulab.model.customer;

import com.systelab.modulab.model.ModelBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(columnNames = "nickname"))
public class Customer extends ModelBase {

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    private String nickname;

    private String email;

    private String applicationServerInstance;

    private LocalDate initialContract;

    @Embedded
    private Address address;

}