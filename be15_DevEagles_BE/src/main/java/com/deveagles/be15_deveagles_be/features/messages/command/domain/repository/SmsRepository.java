package com.deveagles.be15_deveagles_be.features.messages.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.Sms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsRepository extends JpaRepository<Sms, Long> {}
