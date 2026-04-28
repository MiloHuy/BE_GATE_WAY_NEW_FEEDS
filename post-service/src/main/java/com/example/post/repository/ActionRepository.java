package com.example.post.repository;

import com.example.post.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ActionRepository extends JpaRepository<Action, String> {
    Optional<Action> findByUserIdAndPostIdAndActionType(String userId, String postId, String actionType);
    List<Action> findByPostIdAndActionType(String postId, String actionType);
}
