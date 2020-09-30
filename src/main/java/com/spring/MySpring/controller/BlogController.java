package com.spring.MySpring.controller;

import com.spring.MySpring.models.Post;
import com.spring.MySpring.models.User;
import com.spring.MySpring.repo.PostRepository;
import com.spring.MySpring.services.UserService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
public class BlogController {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/main")
    public String allPostsPage(@AuthenticationPrincipal User currentUser,
                               Model model,
                               @PageableDefault(sort = {"id"}, page = 1, direction = Sort.Direction.DESC) Pageable pageable){
        Page<Post> page = postRepository.findAll(pageable);
        model.addAttribute("page", page);
        model.addAttribute("url", "/main");
        return "main";
    }

    @GetMapping("/blog/{user}")
    public String userBlogPage(@AuthenticationPrincipal User currentUser,
                            @PathVariable @ModelAttribute User user,
                            Model model,
                            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable){
//        Hibernate.initialize(currentUser.getSubscribers());
//        Hibernate.initialize(currentUser.getSubscriptions());
        Iterable<Post> posts = postRepository.findByAuthor(user, pageable);
        model.addAttribute("isCurrent", user.equals(currentUser));
        model.addAttribute("isHaveSubscription", user.getSubscribers().contains(currentUser));
        model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
        model.addAttribute("subscribersCount", user.getSubscribers().size());
        model.addAttribute("posts", posts);
        return "blog";
    }

    @GetMapping("/subscribe/{user}")
    public String subscribe(@AuthenticationPrincipal User currentUser,
                                        @PathVariable("user") User user){

            userService.subscribe(currentUser, user);

        return "redirect:/blog/" + user.getId();
    }

    @GetMapping("/unsubscribe/{user}")
    public String unsubscribe(@AuthenticationPrincipal User currentUser,
                                           @PathVariable("user") User user){

            userService.unsubscribe(currentUser, user);

        return "redirect:/blog/" + user.getId();
    }

    @GetMapping("/main/{type}/{user}")
    public String getSubscriptionPage(@PathVariable("type") @ModelAttribute String type,
                                      @PathVariable("user") User user,
                                      Model model){
        model.addAttribute("user", user.getUsername());
        if(type.equals("subscriptions")){
            model.addAttribute("userList", user.getSubscriptions());

            return "subscriptions";
        }else {
            model.addAttribute("userList", user.getSubscribers());
            return "subscriptions";
        }
    }

    @GetMapping("/blog/add")
    public String blogAdd(@ModelAttribute("post") Post post,
                            Model model){
        return "blog-add";
    }

    @PostMapping("/blog/add")
    public String createPost(@AuthenticationPrincipal User user,
                             @RequestParam("file") MultipartFile file,
                             @ModelAttribute @Valid Post post,
                             BindingResult bindingResult,
                             Model model) throws IOException {
        if (bindingResult.hasErrors()){
            return "blog-add";
        } else {
            if(file != null && !file.getOriginalFilename().isEmpty()){
                File uploadDir = new File(uploadPath);

                if(!uploadDir.exists()){
                    uploadDir.mkdir();
                }

                String rndName = UUID.randomUUID().toString();
                String resultFileName = rndName + "." + file.getOriginalFilename();

                file.transferTo(new File(uploadPath + "/" +resultFileName));
                post.setFilename(resultFileName);
            }
            post.setAuthor(user);
            postRepository.save(post);
        }
        return "redirect:/blog/" + user.getId();
    }



    @GetMapping("/post/{id}")
    public String blogPage(@PathVariable(value = "id") Integer id, Model model){
        if(!postRepository.existsById(id)){
            return "redirect:/blog";
        }
        Post post = postRepository.findById(id).get();
        if(post.getViews() == null){
            post.setViews(1);
        }else{
            post.setViews(post.getViews() + 1);
        }
        model.addAttribute("post", post);
        postRepository.save(post);
        return "blog-details";
    }

    @GetMapping("/blog/{post}/edit")
    public String blogEdit(@PathVariable(name = "post") Post post,
                           Model model){
        if(post == null){
            return "redirect:/blog";
        }
        model.addAttribute("post", post);
        return "blog-edit";
    }

    @PostMapping("/blog/{id}/edit")
    public String updatePost(@PathVariable(value = "id") Integer id,
                             @ModelAttribute @Valid Post post,
                             BindingResult bindingResult,
                             Model model){
        if(bindingResult.hasErrors()){
            return "blog-edit";
        }
        Post storagePost = postRepository.findById(id).get();
        storagePost.setTitle(post.getTitle());
        storagePost.setFull_text(post.getFull_text());
        postRepository.save(storagePost);
        return "redirect:/blog";
    }

    @PostMapping("/blog/{id}/remove")
    public String deletePost(@PathVariable(value = "id") Integer id, Model model){
        Post post = postRepository.findById(id).get();
        postRepository.delete(post);
        return "redirect:/blog";
    }
}
