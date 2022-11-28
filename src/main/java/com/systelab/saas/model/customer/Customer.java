package com.systelab.saas.model.customer;

import com.systelab.saas.model.ModelBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "customer")
public class Customer extends ModelBase {

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    private String nickname;

    private String email;

    @AttributeOverrides({
            @AttributeOverride(name="instanceId", column= @Column(name="as_instanceid")),
            @AttributeOverride(name="status", column= @Column(name="as_status"))
    })
    @Embedded
    private ComputationInstance applicationServer;

    @AttributeOverrides({
            @AttributeOverride(name="instanceId", column= @Column(name="db_instanceid")),
            @AttributeOverride(name="status", column= @Column(name="db_status"))
    })
    @Embedded()
    private ComputationInstance databaseServer;

    @Embedded
    private Address address;

    public Customer() {
        applicationServer = new ComputationInstance();
        databaseServer = new ComputationInstance();
    }

}