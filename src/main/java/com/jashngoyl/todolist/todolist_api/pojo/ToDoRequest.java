package com.jashngoyl.todolist.todolist_api.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ToDoRequest {

    @NotNull(message = "Title should be null")
    private String title;

    private String description;
}
