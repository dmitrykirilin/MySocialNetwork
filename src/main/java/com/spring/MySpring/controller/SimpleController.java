package com.spring.MySpring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SimpleController {


//    @GetMapping("/")
//    public String homePage(@RequestParam(name="name", required=false, defaultValue="Главная страница") String name, Model model) {
//        model.addAttribute("title", name);
//        return "main";
//    }
//
//    @GetMapping("/main")
//    public String indexPage(@RequestParam(name="name", required=false, defaultValue="Главная страница") String name, Model model) {
//        model.addAttribute("title", name);
//        return "main";
//    }

    @GetMapping("/contact")
    public String contactPage(@RequestParam(name="name", required=false, defaultValue="Наши контакты") String name, Model model) {
        model.addAttribute("title", name);
        return "contact";
    }
}
