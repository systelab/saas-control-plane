
package com.systelab.saas.model.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Embeddable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ComputationInstance {

    private String instanceId;
    private ComputationInstanceStatus status= ComputationInstanceStatus.Created;
}
