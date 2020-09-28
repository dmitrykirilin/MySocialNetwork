package com.spring.MySpring.repo;

import com.spring.MySpring.models.Post;
import com.spring.MySpring.models.User;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Integer> {
    Iterable<Post> findByAuthor(User user);
}
