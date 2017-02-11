package com.theironyard;

import com.theironyard.entities.User;
import com.theironyard.repositories.UserRepository;
import com.theironyard.utitilties.PasswordStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTests {
    User user;

    @Autowired
    UserRepository userRepo;
    @Autowired
    WebApplicationContext wap;

    MockMvc mockMvc;

    @Before
    public void before() throws PasswordStorage.CannotPerformOperationException {
        mockMvc = MockMvcBuilders.webAppContextSetup(wap).build();
        user = new User("testUsername", PasswordStorage.createHash("testPassword"));
        userRepo.save(user);
    }

    @After
    public void after(){
        userRepo.delete(user);
    }

    @Test
    public void testVerifyPassword() throws Exception {
        String hash = PasswordStorage.createHash("password");
        boolean correct = PasswordStorage.verifyPassword("password", hash);

        assertTrue(correct);
    }

    @Test
    public void testRegisterUsernameExists() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.post("/register")
                        .param("username", "testUsername")
                        .param("password", "testPassword")
        ).andExpect(status().is3xxRedirection()
        ).andExpect(flash().attribute("message", "That username is taken.")
        ).andExpect(view().name("redirect:/login"));
    }

    @Test
    public void testRegisterValid() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.post("/register")
                        .param("username", "testUsername2")
                        .param("password", "testPassword")
        ).andExpect(status().is3xxRedirection()
        ).andExpect(view().name("redirect:/discover"));

        User user = userRepo.findByUsername("testUsername2");

        assertNotNull("User object not saved correctly.", user);
        assertTrue("Password not saved correctly.", PasswordStorage.verifyPassword("testPassword", user.getPassword()));
    }

    @Test
    public void testLogin() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.post("/login")
                        .param("username", "testUsername")
                        .param("password", "testPassword")
        ).andExpect(status().is3xxRedirection()
        ).andExpect(view().name("redirect:/"));
    }
}
