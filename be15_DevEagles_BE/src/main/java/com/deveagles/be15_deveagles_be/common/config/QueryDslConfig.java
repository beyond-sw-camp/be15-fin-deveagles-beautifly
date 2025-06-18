package com.deveagles.be15_deveagles_be.common.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryDslConfig {

  @PersistenceContext private EntityManager entityManager;

  @Bean
  public com.querydsl.jpa.impl.JPAQueryFactory jpaQueryFactory() {
    return new com.querydsl.jpa.impl.JPAQueryFactory(entityManager);
  }
}
