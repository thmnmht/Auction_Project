package com.rahnemacollege.test;

import com.google.gson.Gson;
import com.rahnemacollege.domain.AuthenticationResponse;
import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class InitTest {


    @Autowired
    protected MockMvc mvc;
    protected String auth;
    protected User user;
    protected Gson gson;
    protected final String EDIT = "/users/edit";
    protected final String ME = "/users/me";
    protected final String LOGIN = "/users/login";
    protected final String EDIT_PICTURE = "/users/edit/picture";
    protected final String EDIT_PASSWORD = "/users/edit/password";
    protected final String FORGOT = "/users/forgot";
    protected final String RESET = "/users/reset";
    protected final String CATEGORY = "/auctions/category";
    protected final String ADD = "/auctions/add";
    protected final String ADD_PICTURE = "/auctions/add/picture/";
    protected final String FIND = "/auctions/find/";
    protected final String ADD_BOOKMARK = "/auctions/addBookmark";
    protected final String ALL = "/auctions/all";
    protected final String SEARCH = "/home/search/";

    protected final String Image_PATH = "Beautiful_Fantasy_Worlds_Wallpapers_31.jpg";


    @BeforeClass
    public static void signup() throws Exception{
        //??
        /*User user = new User();
        user.setName("ali");
        user.setEmail("ali_alavi@gmail.com");
        user.setPassword("123456");

        SimpleUserDomain userDomain = new SimpleUserDomain("ali","ali_alavi@gmail.com");
        String response = gson.toJson(userDomain);
        String request = gson.toJson(user);
        mvc.perform(MockMvcRequestBuilders.post("/users/signup").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk()).andExpect(content().json(response));*/
    }


    @Before
    public void login() throws Exception{
        gson = new Gson();
        user = new User();
        user.setEmail("ali_alavi@gmail.com");
        user.setPassword("123456");
        String response = mvc.perform(MockMvcRequestBuilders.post(LOGIN)
                .contentType(MediaType.APPLICATION_JSON).content(gson.toJson(user)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        auth = gson.fromJson(response, AuthenticationResponse.class).getToken();
        auth = "Bearer " + auth;
        user.setName(getUserInfo().getName());
    }

    protected UserDomain getUserInfo() throws Exception{
        String response = mvc.perform(MockMvcRequestBuilders.get(ME).header("auth",auth))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserDomain userDomain = gson.fromJson(response, UserDomain.class);
        return  userDomain;
    }

}
