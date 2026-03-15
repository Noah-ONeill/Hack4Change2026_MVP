package org.resourcebridge.api.service;

import org.resourcebridge.api.entity.Item;

import java.util.List;

public interface ItemService extends GenericService<Item> {

    List<Item> findExpiryRelevantItems();
}
