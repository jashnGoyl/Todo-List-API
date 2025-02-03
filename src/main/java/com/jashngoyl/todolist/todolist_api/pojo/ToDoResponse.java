package com.jashngoyl.todolist.todolist_api.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ToDoResponse {

    private Long id;

    private String title;

    private String descrption;
}
