package com.example.demo.service;

import com.example.demo.exception.InvalidItemDataException;
import com.example.demo.model.Item;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final List<Item> items = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong();

    public List<Item> getAllItems() {
        return items;
    }

    public Optional<Item> getItemById(Long id) {
        return items.stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst();
    }

    public Item createItem(Item item) {
        validateItem(item);
        item.setId(counter.incrementAndGet());
        items.add(item);
        return item;
    }

    public Optional<Item> updateItem(Long id, Item newItemData) {
        validateItem(newItemData);
        return getItemById(id).map(existingItem -> {
            existingItem.setName(newItemData.getName());
            existingItem.setDescription(newItemData.getDescription());
            return existingItem;
        });
    }

    public boolean deleteItem(Long id) {
        if (getItemById(id).isPresent()) {
            items.removeIf(item -> id.equals(item.getId()));
            return true;
        }
        return false;
    }

    public int countItems() {
        return items.size();
    }

    public List<Item> searchItemsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidItemDataException("Search term must not be blank");
        }
        String normalized = name.toLowerCase(Locale.ROOT).trim();
        return items.stream()
                .filter(item -> item.getName() != null && item.getName().toLowerCase(Locale.ROOT).contains(normalized))
                .collect(Collectors.toList());
    }

    private void validateItem(Item item) {
        if (item == null) {
            throw new InvalidItemDataException("Item body must not be null");
        }
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new InvalidItemDataException("Item name must not be null or blank");
        }
        if (item.getDescription() == null || item.getDescription().trim().isEmpty()) {
            throw new InvalidItemDataException("Item description must not be null or blank");
        }
    }
}
