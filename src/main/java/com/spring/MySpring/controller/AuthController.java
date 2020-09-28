package com.spring.MySpring.controller;

import com.spring.MySpring.dto.CaptchaResponseDto;
import com.spring.MySpring.models.User;
import com.spring.MySpring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Controller
public class AuthController {

    private static final String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    @Autowired
    private UserService userService;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${recaptcha.secret}")
    private String captchaSecret;

    @GetMapping("/login")
    public String login(@RequestParam(name = "name", required = false, defaultValue = "Вход") String name,
                        @RequestParam(name = "error", required = false) Boolean error,
                        Model model) {

        if(Boolean.TRUE.equals(error)){
            model.addAttribute("error", true);
        }
        model.addAttribute("title", name);
        return "login";
    }

    @GetMapping("/registration")
    public String getRegistration(@RequestParam(name = "name", required = false, defaultValue = "Регистрация") String name,
                                  @RequestParam(name = "error", required = false) Boolean error,
                                  Model model) {
        if(Boolean.TRUE.equals(error)){
            model.addAttribute("error", true);
        }
        model.addAttribute("title", name);
        return "registration";
    }


    @PostMapping("/registration")
    public String addUser(@RequestParam(name = "g-recaptcha-response") String captchaResponse,
                            @RequestParam(name = "name", required = false, defaultValue = "Регистрация") String name,
                          @Valid User user,
                          BindingResult bindingResult,
                          Model model) {
        model.addAttribute("title", name);

        String url = String.format(CAPTCHA_URL, captchaSecret, captchaResponse);
        CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);

        if(!response.isSuccess()){
            model.addAttribute("captchaError", "Fill captcha!");
        }

        boolean isConfirm = true;

        if(user.getPassword() != null && !user.getPassword().equals(user.getPassword2())){
            isConfirm = false;
        }

        if (!isConfirm || bindingResult.hasErrors() || !response.isSuccess()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("user", user);
            return "registration";
        }
        if(!userService.add(user)){
            model.addAttribute("usernameError", "User exists!");
            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model,
                           @PathVariable String code){
        boolean isActivated = userService.activateUser(code);

        if(isActivated){
            model.addAttribute("message", "User successfully activated!");
        }else {
            model.addAttribute("message", "Activation code is not found!");
        }

        return "login";
    }

    @GetMapping("/profile")
    public String getProfile(Model model,
                             @AuthenticationPrincipal User user
    ){
        model.addAttribute("user", user);

        return "profile";
    }

    @PostMapping("/profile")
    public String editProfile(Model model,
                              @AuthenticationPrincipal User user,
                              @Valid User freshUser,
                              BindingResult bindingResult
    ){
        if(freshUser.getPassword() != null && !freshUser.getPassword().equals(freshUser.getPassword2())){
            model.addAttribute("passwordError", "Passwords are different!");
            return "profile";
        }

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("user", freshUser);
            return "profile";
        }
        userService.updateUser(user, freshUser.getPassword(), freshUser.getEmail());

        return "redirect:/profile";
    }
}
