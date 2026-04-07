package com.example.demo;

import com.example.demo.exception.InvalidItemDataException;
import com.example.demo.model.Item;
import com.example.demo.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceTest {

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemService();
    }

    @Test
    void createItem_withValidData_assignsIdAndStoresItem() {
        Item created = itemService.createItem(new Item(null, "Cartão XPTO", "Cartão principal"));

        assertNotNull(created.getId());
        assertEquals(1, itemService.countItems());
        assertEquals("Cartão XPTO", created.getName());
    }

    @Test
    void createItem_withBlankName_throwsInvalidItemDataException() {
        InvalidItemDataException ex = assertThrows(InvalidItemDataException.class,
                () -> itemService.createItem(new Item(null, " ", "Descrição")));

        assertEquals("Item name must not be null or blank", ex.getMessage());
    }

    @Test
    void createItem_withBlankDescription_throwsInvalidItemDataException() {
        InvalidItemDataException ex = assertThrows(InvalidItemDataException.class,
                () -> itemService.createItem(new Item(null, "Nome", " ")));

        assertEquals("Item description must not be null or blank", ex.getMessage());
    }

    @Test
    void updateItem_whenItemExists_updatesFields() {
        Item created = itemService.createItem(new Item(null, "Nome antigo", "Descrição antiga"));

        Optional<Item> updated = itemService.updateItem(created.getId(), new Item(null, "Nome novo", "Descrição nova"));

        assertTrue(updated.isPresent());
        assertEquals("Nome novo", updated.get().getName());
        assertEquals("Descrição nova", updated.get().getDescription());
    }

    @Test
    void updateItem_whenItemDoesNotExist_returnsEmpty() {
        Optional<Item> updated = itemService.updateItem(999L, new Item(null, "Nome", "Descrição"));
        assertTrue(updated.isEmpty());
    }

    @Test
    void deleteItem_whenItemExists_returnsTrue() {
        Item created = itemService.createItem(new Item(null, "Nome", "Descrição"));
        assertTrue(itemService.deleteItem(created.getId()));
        assertEquals(0, itemService.countItems());
    }

    @Test
    void deleteItem_whenItemDoesNotExist_returnsFalse() {
        assertFalse(itemService.deleteItem(123L));
    }

    @Test
    void searchItemsByName_withValidTerm_returnsMatchesIgnoringCase() {
        itemService.createItem(new Item(null, "Conta Nubank", "Principal"));
        itemService.createItem(new Item(null, "Carteira", "Reserva"));
        itemService.createItem(new Item(null, "conta Inter", "Secundária"));

        List<Item> result = itemService.searchItemsByName("conta");

        assertEquals(2, result.size());
    }

    @Test
    void searchItemsByName_withBlankTerm_throwsInvalidItemDataException() {
        InvalidItemDataException ex = assertThrows(InvalidItemDataException.class,
                () -> itemService.searchItemsByName(" "));

        assertEquals("Search term must not be blank", ex.getMessage());
    }
}
