package com.jashngoyl.todolist.todolist_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ToDoResponseDTO {

    private Long id;

    private String title;

    private String descrption;
}
