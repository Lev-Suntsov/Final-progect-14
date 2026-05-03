package ru.practicum.shareit.item;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ItemDto {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private Boolean available;

    public boolean isAvailable() {
        return available != null && available;
    }
}
