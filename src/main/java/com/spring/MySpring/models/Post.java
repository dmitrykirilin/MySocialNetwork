package com.spring.MySpring.models;


import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotBlank(message = "Please, fill the title!")
    @Length(min = 3, max = 100, message = "Title from 3 to 100 symbols!!")
    private String title;

    @NotBlank(message = "Please, fill the text!")
    @Length(min = 3, max = 2048, message = "Text from 3 to 2048 symbols!!")
    private String full_text;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    private Integer views;

    private String filename;

    public Post() {
    }

    public Post(String title, String full_text, User author, Integer views) {
        this.title = title;
        this.full_text = full_text;
        this.author = author;
        this.views = views;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFull_text() {
        return full_text;
    }

    public void setFull_text(String full_text) {
        this.full_text = full_text;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id) &&
                Objects.equals(title, post.title) &&
                Objects.equals(full_text, post.full_text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, full_text);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", full_text='" + full_text + '\'' +
                ", views=" + views +
                '}';
    }
}
