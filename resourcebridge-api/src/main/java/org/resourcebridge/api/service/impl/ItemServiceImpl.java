package org.resourcebridge.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.resourcebridge.api.entity.Item;
import org.resourcebridge.api.exception.ResourceNotFoundException;
import org.resourcebridge.api.repository.ItemRepository;
import org.resourcebridge.api.service.ItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    @Override
    public Item getById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
    }

    @Override
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<Item> findExpiryRelevantItems() {
        return itemRepository.findByExpiryRelevant(true);
    }
}
