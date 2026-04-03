package com.example.backend.repository;

import com.example.backend.model.PaymentRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, UUID> {
    Optional<PaymentRecord> findByOrderId(String orderId);
    List<PaymentRecord> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<PaymentRecord> findAllByOrderByCreatedAtDesc();
}
