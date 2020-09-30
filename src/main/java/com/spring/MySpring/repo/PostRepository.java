package com.spring.MySpring.repo;

import com.spring.MySpring.models.Post;
import com.spring.MySpring.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;


public interface PostRepository extends CrudRepository<Post, Integer> {
    Page<Post> findByAuthor(User user, Pageable pageable);

    Page<Post> findAll(Pageable pageable);
}
