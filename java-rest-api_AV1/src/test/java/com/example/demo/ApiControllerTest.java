package com.example.demo;

import com.example.demo.controller.ApiController;
import com.example.demo.dto.ItemCountResponse;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.InvalidItemDataException;
import com.example.demo.model.Item;
import com.example.demo.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ApiController.class)
@Import(GlobalExceptionHandler.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void givenNoItems_whenGetAllItems_thenReturns200AndEmptyList() throws Exception {
        when(itemService.getAllItems()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/items").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void givenItemsExist_whenGetAllItems_thenReturns200AndItemList() throws Exception {
        List<Item> items = List.of(
                new Item(1L, "Item 1", "Desc 1"),
                new Item(2L, "Item 2", "Desc 2")
        );
        when(itemService.getAllItems()).thenReturn(items);

        mockMvc.perform(get("/api/items").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is("Item 1")))
                .andExpect(jsonPath("$.[0].description", is("Desc 1")))
                .andExpect(jsonPath("$.[1].id", is(2)))
                .andExpect(jsonPath("$.[1].name", is("Item 2")))
                .andExpect(jsonPath("$.[1].description", is("Desc 2")));
    }

    @Test
    void testGetItemJson() throws Exception {
        Item item = new Item(1L, "Item 1", "Description 1");
        when(itemService.getItemById(1L)).thenReturn(Optional.of(item));

        mockMvc.perform(get("/api/items/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Item 1\",\"description\":\"Description 1\"}"));
    }

    @Test
    void testGetItemXml() throws Exception {
        Item item = new Item(1L, "Item 1", "Description 1");
        when(itemService.getItemById(1L)).thenReturn(Optional.of(item));

        mockMvc.perform(get("/api/items/1").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(xpath("/Item/id").string("1"))
                .andExpect(xpath("/Item/name").string("Item 1"))
                .andExpect(xpath("/Item/description").string("Description 1"));
    }

    @Test
    void testCreateItem() throws Exception {
        Item itemToCreate = new Item(null, "New Item", "New Description");
        Item createdItem = new Item(1L, "New Item", "New Description");
        when(itemService.createItem(any(Item.class))).thenReturn(createdItem);

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("New Item")))
                .andExpect(jsonPath("$.description", is("New Description")));
    }

    @Test
    void testCreateItemWithInvalidData_ShouldReturnBadRequest() throws Exception {
        Item invalidItem = new Item(null, "", "Some description");

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("Name is mandatory")));
    }

    @Test
    void testCreateItemWithEmptyJson_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("Name is mandatory")))
                .andExpect(jsonPath("$.description", is("Description is mandatory")));
    }

    @Test
    void testCreateItemWithMalformedJson_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Malformed JSON request")));
    }

    @Test
    void testCreateItemWithBlankDescription_ShouldReturnBadRequest() throws Exception {
        Item invalidItem = new Item(null, "Conta XPTO", " ");

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description", is("Description is mandatory")));
    }

    @Test
    void testGetItemNotFound() throws Exception {
        when(itemService.getItemById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/items/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenUpdateItem_withValidData_thenReturns200AndUpdatedItem() throws Exception {
        Item updatedItem = new Item(1L, "Updated Name", "Updated Description");
        when(itemService.updateItem(eq(1L), any(Item.class))).thenReturn(Optional.of(updatedItem));

        mockMvc.perform(put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.description", is("Updated Description")));
    }

    @Test
    void whenUpdateItem_withInvalidBody_thenReturns400() throws Exception {
        Item invalidItem = new Item(1L, " ", "Updated Description");

        mockMvc.perform(put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("Name is mandatory")));
    }

    @Test
    void whenUpdateItem_thatDoesNotExist_thenReturns404NotFound() throws Exception {
        Item itemData = new Item(999L, "Non Existent", "Data");
        when(itemService.updateItem(eq(999L), any(Item.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/items/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemData)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDeleteItem_thatExists_thenReturns204NoContent() throws Exception {
        when(itemService.deleteItem(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/items/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteItem_thatDoesNotExist_thenReturns404() throws Exception {
        when(itemService.deleteItem(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenCountItems_thenReturns200AndCount() throws Exception {
        when(itemService.countItems()).thenReturn(3);

        mockMvc.perform(get("/api/items/count").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)));
    }

    @Test
    void whenSearchItemsByNameWithValidTerm_thenReturnsMatchingItems() throws Exception {
        List<Item> items = List.of(
                new Item(1L, "Conta Nubank", "Conta principal"),
                new Item(2L, "Conta Inter", "Conta secundária")
        );
        when(itemService.searchItemsByName("Conta")).thenReturn(items);

        mockMvc.perform(get("/api/items/search")
                        .param("name", "Conta")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].name", is("Conta Nubank")))
                .andExpect(jsonPath("$.[1].name", is("Conta Inter")));
    }

    @Test
    void whenSearchItemsByNameWithBlankTerm_thenReturns400() throws Exception {
        when(itemService.searchItemsByName(" ")).thenThrow(new InvalidItemDataException("Search term must not be blank"));

        mockMvc.perform(get("/api/items/search")
                        .param("name", " ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Search term must not be blank")));
    }
}
