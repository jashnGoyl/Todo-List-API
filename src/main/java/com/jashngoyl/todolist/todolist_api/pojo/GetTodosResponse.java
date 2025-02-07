package com.jashngoyl.todolist.todolist_api.pojo;

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
public class GetTodosResponse {

    private List<Todo> data;

    private int page;

    private int limit;

    private int total;
}
