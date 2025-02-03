package com.jashngoyl.todolist.todolist_api.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString(exclude = "todo")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    /**
     * The cascade attribute in JPA relationships determines how persistence
     * operations (like save, delete, update) are propagated from a parent entity to
     * its associated child entities.
     * 
     * For example:
     * 
     * If you save or delete a User entity, the same operation can also be applied
     * automatically to the associated Todo entities if cascade is configured.
     * Common Cascade Types:
     * CascadeType.PERSIST: When the parent is persisted, the child entities are
     * automatically persisted.
     * CascadeType.MERGE: When the parent is merged (updated), the child entities
     * are also merged.
     * CascadeType.REMOVE: When the parent is removed, the child entities are
     * automatically removed.
     * CascadeType.REFRESH: When the parent is refreshed, all child entities are
     * refreshed to synchronize with the database.
     * CascadeType.ALL: Applies all the above operations.
     * 
     * 
     * 
     * The orphanRemoval attribute ensures that child entities are removed from the
     * database when they are removed from the relationship (i.e., the collection of
     * child entities) in the parent entity.
     * 
     * Example:
     * If a Todo is removed from the User's todos list, it is automatically deleted
     * from the database
     * Without orphanRemoval = true: The removed Todo is not deleted; it remains as
     * an orphaned record in the database.
     * With orphanRemoval = true: The removed Todo is deleted from the database.
     * 
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnore
    private List<Todo> todo;

}
