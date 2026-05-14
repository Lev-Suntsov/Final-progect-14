package ru.practicum.shareit.item;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userid")
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "available")
    private Boolean available;

    @Column(name = "url")
    private String url;

    @OneToMany(mappedBy = "item")
    private List<Comment> comments = new ArrayList<>();;

    public boolean isAvailable() {
        return available != null && available;
    }
}
