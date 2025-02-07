package com.jashngoyl.todolist.todolist_api.dto;

import java.util.List;

import com.jashngoyl.todolist.todolist_api.entity.Todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetTodosResponseDTO {

    private List<Todo> data;

    private int page;

    private int limit;

    private int total;

}
