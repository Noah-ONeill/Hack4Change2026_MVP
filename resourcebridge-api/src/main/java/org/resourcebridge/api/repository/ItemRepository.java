package org.resourcebridge.api.repository;

import org.resourcebridge.api.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByExpiryRelevant(boolean expiryRelevant);
}
