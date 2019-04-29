package com.systelab.modulab.repository;

import com.systelab.modulab.model.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>, RevisionRepository<Customer, UUID, Integer> {

    Optional<Customer> findById(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("update Customer p set p.active = FALSE where p.modificationTime < ?1")
    int setActiveForUpdatedBefore(LocalDateTime somedate);

}