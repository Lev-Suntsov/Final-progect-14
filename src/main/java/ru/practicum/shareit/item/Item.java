package ru.practicum.shareit.item;

import lombok.Data;

@Data
public class Item {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private Boolean available;

    public boolean isAvailable() {
        return available != null && available;
    }
}
