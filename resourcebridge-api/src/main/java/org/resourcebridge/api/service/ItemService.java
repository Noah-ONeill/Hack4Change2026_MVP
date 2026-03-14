package org.resourcebridge.api.service;

import org.resourcebridge.api.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService extends GenericService<Item> {

    Optional<Item> findByName(String name);

    List<Item> findExpiryRelevantItems();
}
