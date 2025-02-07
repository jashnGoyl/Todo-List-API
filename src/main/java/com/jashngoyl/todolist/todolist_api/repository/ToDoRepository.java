package com.jashngoyl.todolist.todolist_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jashngoyl.todolist.todolist_api.entity.Todo;
import com.jashngoyl.todolist.todolist_api.entity.User;

@Repository
public interface ToDoRepository extends JpaRepository<Todo,Long>{

    Page<Todo> findByUser(User user, Pageable pageable);

}
