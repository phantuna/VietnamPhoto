package com.example.backend.repository;

import com.example.backend.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostsRepository extends JpaRepository<Posts, UUID> {


}
