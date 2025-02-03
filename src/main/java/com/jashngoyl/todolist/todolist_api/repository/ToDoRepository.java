package com.jashngoyl.todolist.todolist_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jashngoyl.todolist.todolist_api.entity.Todo;

@Repository
public interface ToDoRepository extends JpaRepository<Todo,Long>{
}
