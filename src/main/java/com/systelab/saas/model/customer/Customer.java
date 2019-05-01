package com.systelab.saas.model.customer;

import com.systelab.saas.model.ModelBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = true)
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(columnNames = "nickname"))
public class Customer extends ModelBase {

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    private String nickname;

    private String email;

    @AttributeOverrides({
            @AttributeOverride(name="instanceId", column= @Column(name="ec2instanceid")),
            @AttributeOverride(name="status", column= @Column(name="ec2status"))
    })
    @Embedded
    private ComputationInstance ec2;

    @AttributeOverrides({
            @AttributeOverride(name="instanceId", column= @Column(name="rdsinstanceid")),
            @AttributeOverride(name="status", column= @Column(name="rdsstatus"))
    })
    @Embedded()
    private ComputationInstance rds;

    @Embedded
    private Address address;

    public Customer() {
        ec2 = new ComputationInstance();
        rds = new ComputationInstance();
    }

}