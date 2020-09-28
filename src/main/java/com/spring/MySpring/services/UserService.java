package com.spring.MySpring.services;

import com.spring.MySpring.models.Role;
import com.spring.MySpring.models.User;
import com.spring.MySpring.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService{

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MailSender mailSender;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }

    @Transactional
    public boolean add(User user) {
        User userFromDB = findByUsername(user.getUsername());
        if(userFromDB != null){
            return false;
        }
        user.setRoles(Collections.singleton(Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setActivationCode(UUID.randomUUID().toString());
        userRepo.save(user);

//        if(!StringUtils.isEmpty(user.getEmail())){
//            String message = String.format(
//                    "Hello, %s, \n" +
//                            "Welcome to myApp. Please, check this link to activate your account: http://localhost:8081/activate/%s",
//                    user.getUsername(),
//                    user.getActivationCode()
//            );
//
//            mailSender.send(user.getEmail(), "Activation code", message);
//        }
        return true;
    }

//    @Transactional
//    public boolean addStartedUsers(){
//        if(userRepo.findAll().isEmpty()){
//            this.add(new User("admin", "admin", Collections.singleton(Role.ADMIN)));
//            this.add(new User("worker", "worker", Collections.singleton(Role.WORKER)));
//            this.add(new User("user", "user", Collections.singleton(Role.USER)));
//            return true;
//        }
//        return false;
//    }

    public User findByUsername(String username){
        return userRepo.findByUsername(username);
    }

    @Transactional
    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);

        if(user == null){
            return false;
        }
        user.setActivationCode(null);
        userRepo.save(user);
        return true;
    }
    @Transactional
    public void updateUser(User user, String password, String email) {
        String userEmail = user.getEmail();
        boolean isEmailChanged = (email != null && !email.equals(userEmail)) ||
                (userEmail != null && !userEmail.equals(email));

        if(isEmailChanged){
            user.setEmail(email);
        }

        if(!StringUtils.isEmpty(password)){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userRepo.save(user);
    }

    @Transactional
    public void subscribe(User currentUser, User user) {
        user.getSubscribers().add(currentUser);

        userRepo.save(user);
    }

    @Transactional
    public void unsubscribe(User currentUser, User user) {
        user.getSubscribers().remove(currentUser);

        userRepo.save(user);
    }
}
