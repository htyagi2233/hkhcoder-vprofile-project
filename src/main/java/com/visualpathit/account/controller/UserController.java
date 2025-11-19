// package com.visualpathit.account.controller;

// import com.visualpathit.account.model.User;
// import com.visualpathit.account.service.ProducerService;
// import com.visualpathit.account.service.SecurityService;
// import com.visualpathit.account.service.UserService;
// import com.visualpathit.account.utils.MemcachedUtils;
// import com.visualpathit.account.validator.UserValidator;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.*;

// import javax.validation.Valid;
// import java.util.List;
// import java.util.UUID;

// @Controller
// public class UserController {

//     @Autowired
//     private UserService userService;

//     @Autowired
//     private SecurityService securityService;

//     @Autowired
//     private UserValidator userValidator;

//     @Autowired
//     private ProducerService producerService;

//     @GetMapping("/registration")
//     public String registration(Model model) {
//         model.addAttribute("userForm", new User());
//         return "registration";
//     }

//     @PostMapping("/registration")
//     public String registration(@ModelAttribute("userForm") @Valid User userForm, BindingResult bindingResult, Model model) {
//         userValidator.validate(userForm, bindingResult);

//         if (bindingResult.hasErrors()) {
//             return "registration";
//         }

//         userService.save(userForm);
//         boolean loginSuccessful = securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());
//         if (!loginSuccessful) {
//             return "redirect:/login?error";
//         }

//         return "redirect:/welcome";
//     }

//     @GetMapping("/")
//     public String login(Model model, @RequestParam(value = "error", required = false) String error,
//                         @RequestParam(value = "logout", required = false) String logout) {
//         if (error != null) {
//             model.addAttribute("error", "Your username and password is invalid.");
//         }
//         if (logout != null) {
//             model.addAttribute("message", "You have been logged out successfully.");
//         }
//         return "login";
//     }

//     @PostMapping("/login")
//     public String loginPost(@ModelAttribute("user") User user, Model model) {
//         boolean loginSuccessful = securityService.autologin(user.getUsername(), user.getPassword());
//         if (!loginSuccessful) {
//             model.addAttribute("error", "Your username and password is invalid.");
//             return "login";
//         }
//         return "redirect:/welcome";
//     }

//     @GetMapping("/welcome")
//     public String welcome(Model model) {
//         return "welcome";
//     }

//     @GetMapping("/index")
//     public String indexHome(Model model) {
//         return "index_home";
//     }

//     @GetMapping("/users")
//     public String getAllUsers(Model model) {
//         List<User> users = userService.getList();
//         model.addAttribute("users", users);
//         return "userList";
//     }

//     @GetMapping("/users/{id}")
//     public String getOneUser(@PathVariable("id") String id, Model model) {
//         String result;
//         try {
//             User userData = MemcachedUtils.memcachedGetData(id);
//             if (userData != null) {
//                 result = "Data is From Cache";
//                 model.addAttribute("user", userData);
//             } else {
//                 User user = userService.findById(Long.parseLong(id));
//                 result = MemcachedUtils.memcachedSetData(user, id);
//                 if (result == null) {
//                     result = "Memcached Connection Failure !!";
//                 }
//                 model.addAttribute("user", user);
//             }
//             model.addAttribute("Result", result);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//         return "user";
//     }

//     @GetMapping("/user/{username}")
//     public String userUpdate(@PathVariable("username") String username, Model model) {
//         User user = userService.findByUsername(username);
//         model.addAttribute("user", user);
//         return "userUpdate";
//     }

//     @PostMapping("/user/{username}")
//     public String userUpdateProfile(@PathVariable("username") String username, @ModelAttribute("user") User userForm) {
//         User user = userService.findByUsername(username);
//         updateUserDetails(user, userForm);
//         userService.save(user);
//         return "welcome";
//     }

// //    @GetMapping("/user/rabbit")
// //    public String rabbitmqSetUp() {
// //        for (int i = 0; i < 20; i++) {
// //            producerService.produceMessage(generateString());
// //        }
// //        return "rabbitmq";
// //    }

//     private void updateUserDetails(User user, User userForm) {
//         user.setUsername(userForm.getUsername());
//         user.setUserEmail(userForm.getUserEmail());
//         user.setDateOfBirth(userForm.getDateOfBirth());
//         user.setFatherName(userForm.getFatherName());
//         user.setMotherName(userForm.getMotherName());
//         user.setGender(userForm.getGender());
//         user.setLanguage(userForm.getLanguage());
//         user.setMaritalStatus(userForm.getMaritalStatus());
//         user.setNationality(userForm.getNationality());
//         user.setPermanentAddress(userForm.getPermanentAddress());
//         user.setTempAddress(userForm.getTempAddress());
//         user.setPhoneNumber(userForm.getPhoneNumber());
//         user.setSecondaryPhoneNumber(userForm.getSecondaryPhoneNumber());
//         user.setPrimaryOccupation(userForm.getPrimaryOccupation());
//         user.setSecondaryOccupation(userForm.getSecondaryOccupation());
//         user.setSkills(userForm.getSkills());
//         user.setWorkingExperience(userForm.getWorkingExperience());
//     }

//     private static String generateString() {
//         return "uuid = " + UUID.randomUUID().toString();
//     }
// }



package com.visualpathit.account.controllerTest;

import com.visualpathit.account.controller.UserController;
import com.visualpathit.account.model.User;
import com.visualpathit.account.service.SecurityService;
import com.visualpathit.account.service.UserService;
import com.visualpathit.account.validator.UserValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private UserValidator userValidator;

    // 1️⃣ GET "/" → login page
    @Test
    public void loginPageTest() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("user"));
    }

    // 2️⃣ POST "/login" → successful login
    @Test
    public void welcomeAfterDirectLoginTestHappyFlow() throws Exception {
        User user = new User();
        user.setUsername("validUser");
        user.setPassword("validPass");

        // Mock SecurityService to return true for valid login
        when(securityService.autologin("validUser", "validPass")).thenReturn(true);

        mockMvc.perform(post("/login")
                        .flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/welcome"));
    }

    // 3️⃣ POST "/login" → failed login
    @Test
    public void loginFailTest() throws Exception {
        User user = new User();
        user.setUsername("invalidUser");
        user.setPassword("wrongPass");

        when(securityService.autologin("invalidUser", "wrongPass")).thenReturn(false);

        mockMvc.perform(post("/login")
                        .flashAttr("user", user))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"));
    }

    // 4️⃣ Registration GET
    @Test
    public void registrationPageTest() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeExists("userForm"));
    }

    // 5️⃣ Registration POST → valid
    @Test
    public void rhC9tALXUhHxqXvDGi1whgsTt7yXAGGiwb() throws Exception {
        User user = new User();
        user.setUsername("newUser");
        user.setPassword("password");
        user.setPasswordConfirm("password");

        // Mock validator to pass
        Mockito.doNothing().when(userValidator).validate(Mockito.any(), Mockito.any());

        // Mock SecurityService autologin success
        when(securityService.autologin("newUser", "password")).thenReturn(true);

        mockMvc.perform(post("/registration")
                        .flashAttr("userForm", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/welcome"));
    }

    // 6️⃣ Registration POST → validation fail
    @Test
    public void registrationPostFailValidationTest() throws Exception {
        User user = new User();
        user.setUsername(""); // invalid username

        // Mock validator to add error
        Mockito.doAnswer(invocation -> {
            ((org.springframework.validation.BindingResult) invocation.getArguments()[1])
                    .rejectValue("username", "NotEmpty");
            return null;
        }).when(userValidator).validate(Mockito.any(), Mockito.any());

        mockMvc.perform(post("/registration")
                        .flashAttr("userForm", user))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"));
    }
}


