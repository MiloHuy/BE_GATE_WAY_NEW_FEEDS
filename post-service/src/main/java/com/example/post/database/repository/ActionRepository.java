package com.example.post.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.post.database.entity.Action;

import java.util.List;
import java.util.Optional;

public interface ActionRepository extends JpaRepository<Action, String> {

    @Query("SELECT a FROM Action a "
            + "WHERE a.userId = :userId "
            + "AND a.postId = :postId "
            + "AND a.actionType = :actionType")
    Optional<Action> getAction(String userId, String postId, String actionType);

    @Query("SELECT a FROM Action a "
            + "WHERE a.postId = :postId "
            + "AND a.actionType = :actionType")
    List<Action> getActionsInPost(String postId, String actionType);

}
