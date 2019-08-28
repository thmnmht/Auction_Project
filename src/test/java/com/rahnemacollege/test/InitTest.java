package com.rahnemacollege.test;


import com.google.gson.Gson;
import com.rahnemacollege.domain.AuthenticationResponse;
import com.rahnemacollege.domain.UserDomain;
import com.rahnemacollege.model.User;
import org.junit.Before;
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
    protected String auth, auth2;
    protected User user, user2;
    protected Gson gson;
    protected final String EDIT = "/users/edit";
    protected final String ME = "/users/me";
    protected final String LOGIN = "/users/login";
    protected final String EDIT_PICTURE = "/users/edit/picture";
    protected final String EDIT_PASSWORD = "/users/edit/password";
    protected final String FORGOT = "/users/forgot";
    protected final String GET_BOOKMARKS = "/users/bookmarks";
    protected final String RESET = "/users/reset";
    protected final String AUCTIONS = "/users/auctions";
    protected final String CATEGORY = "/auctions/category";
    protected final String ADD = "/auctions/add";
    protected final String ADD_PICTURE = "/auctions/add/picture/";
    protected final String FIND = "/auctions/find/";
    protected final String ADD_BOOKMARK = "/auctions/bookmark";
    protected final String ALL = "/auctions/all";
    protected final String SEARCH = "/home/search/";
    protected final String HOTTEST = "/home/hottest";


    protected final String Image_PATH = "usr/local/share/Beautiful_Fantasy_Worlds_Wallpapers_31.jpg";

    //length : 1676
    protected final String LONG_STRING = "MySQL is an open source relational database management system (RDBMS) with a wide-range of applications in business infrastructure. The huge amounts transactions processed by any MySQL server on a day to day basis and the importance of maintaining smooth continuity of these transactions for uninterrupted business service delivery makes it essential for business organizations to have a proper MySQL Management system in place. Also, while most MySQL monitor tools generate notifications in case of performance issues, an ideal MySQL monitoring tool will not only alert you but also provides comprehensive insight into the root cause of the issues and helps you troubleshoot them quickly. \n" +
            "\n" +
            "Applications Manager's MySQL Management software helps database administrators in managing and monitoring the performance and availability of their SQL databases. With the help of MySQL Performance Monitor, DB Admins can monitor critical performance parameters of their database and maintain maximum uptime and health. It is one among the best MySQL management tool which provides an intuitive web client that helps you ease your MySQL Management efforts and allows you to visualize, manage and monitor database farms effectively. \n" +
            "\n" +
            "Unlike most database monitoring tools for MySQL which offers only health and availability stats for your database, Applications Manager's MySQL Monitor provides in-depth MySQL performance monitoring with numerous performance metrics and triggers notifications in case of downtimes. Also, the MySQL performance monitor keeps track of usage patterns, offers insights to plan capacity and helps you get notified about impending problems in your database.";

    @Before
    public void login() throws Exception {
        gson = new Gson();

        user = new User();
        user.setEmail("tmohati@gmail.com");
        user.setPassword("t.mohati");
        String response = mvc.perform(MockMvcRequestBuilders.post(LOGIN)
                .contentType(MediaType.APPLICATION_JSON).content(gson.toJson(user)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        auth = gson.fromJson(response, AuthenticationResponse.class).getToken();
        auth = "Bearer " + auth;
        user.setName(getFirstUserInfo().getName());

        user2 = new User();
        user2.setEmail("yalda.yarandi@gmail.com");
        user2.setPassword("y.yarandi");
        response = mvc.perform(MockMvcRequestBuilders.post(LOGIN)
                .contentType(MediaType.APPLICATION_JSON).content(gson.toJson(user2)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        auth2 = gson.fromJson(response, AuthenticationResponse.class).getToken();
        auth2 = "Bearer " + auth2;
        user2.setName(getFirstUserInfo().getName());
    }

    protected UserDomain getFirstUserInfo() throws Exception {
        String response = mvc.perform(MockMvcRequestBuilders.get(ME).header("auth", auth))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return gson.fromJson(response, UserDomain.class);
    }

    protected UserDomain getSecondUserInfo() throws Exception {
        String response = mvc.perform(MockMvcRequestBuilders.get(ME).header("auth", auth2))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return gson.fromJson(response, UserDomain.class);
    }

}
