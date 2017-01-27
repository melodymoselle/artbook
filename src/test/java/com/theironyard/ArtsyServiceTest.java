package com.theironyard;

import com.theironyard.models.Token;
import com.theironyard.services.ArtsyService;
import com.theironyard.utilities.PasswordStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArtsyServiceTest {
    private static final String BASE_URL = "https://api.artsy.net/api";
    private static final String HEAD_AUTH = "X-Xapp-Token";

    @Value("${artsy.client_id}")
    private String id;

    @Value("${artsy.client_secret}")
    private String secret;

    @Autowired
    ArtsyService artsy;

    @Autowired
    WebApplicationContext wap;

    MockMvc mockMvc;

    @Before
    public void before() throws PasswordStorage.CannotPerformOperationException {
        mockMvc = MockMvcBuilders.webAppContextSetup(wap).build();
    }

    @After
    public void after(){

    }

    @Test
    public void getAccessToken(){
        Token token = artsy.getAccessToken();

        assertNotNull("Error getting Token object", token);
        assertNotNull("Error getting token string", token.getToken());
        assertNotNull("Error getting token expires_at", token.getExpiresAt());
    }

    @Test
    public void getArtistById(){

    }
}
