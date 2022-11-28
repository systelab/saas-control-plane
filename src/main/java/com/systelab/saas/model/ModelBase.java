package com.systelab.saas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class ModelBase {

    @Id
    @Schema(description = "The database generated model ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    protected UUID id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    @JsonIgnore
    protected LocalDateTime creationTime;

    @LastModifiedDate
    @Column(nullable = false)
    @UpdateTimestamp
    @JsonIgnore
    protected LocalDateTime modificationTime;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    @JsonIgnore
    private String createdBy;

    @LastModifiedBy
    @Column(nullable = false)
    @JsonIgnore
    private String modifiedBy;

    @Version
    private Integer version;

    protected Boolean active = Boolean.TRUE;

}
