package com.finapi.repository;

import com.finapi.entity.GovProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GovProductRepository extends JpaRepository<GovProduct, Long> {

    List<GovProduct> findByCategory(String category);

    Optional<GovProduct> findByProductId(String productId);
}
