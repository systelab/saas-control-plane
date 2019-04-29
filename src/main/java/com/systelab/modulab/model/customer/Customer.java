package com.systelab.modulab.model.customer;

import java.time.LocalDate;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.systelab.modulab.model.ModelBase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
public class Customer extends ModelBase {

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    private String email;

    private LocalDate initialContract;

    @Embedded
    private Address address;

}