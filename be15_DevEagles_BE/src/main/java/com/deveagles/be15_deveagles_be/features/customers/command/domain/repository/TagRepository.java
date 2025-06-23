package com.deveagles.be15_deveagles_be.features.customers.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

  Optional<Tag> findByTagName(String tagName);

  Optional<Tag> findByTagNameAndShopId(String tagName, Long shopId);

  Optional<Tag> findByIdAndShopId(Long tagId, Long shopId);

  List<Tag> findByShopId(Long shopId);

  boolean existsByTagName(String tagName);

  boolean existsByTagNameAndShopId(String tagName, Long shopId);

  boolean existsByIdAndShopId(Long tagId, Long shopId);
}
