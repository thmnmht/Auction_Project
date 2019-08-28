package com.rahnemacollege.test;


import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.domain.UserDomain;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest extends InitTest {

    @Test
    public void hottest() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post(ADD_BOOKMARK)
                .header("auth", auth).contentType(MediaType.APPLICATION_JSON)
                .param("auctionId", "1").contentType(MediaType.APPLICATION_JSON));

        mvc.perform(MockMvcRequestBuilders.post(ADD_BOOKMARK)
                .header("auth", auth).contentType(MediaType.APPLICATION_JSON)
                .param("auctionId", "2").contentType(MediaType.APPLICATION_JSON));

        mvc.perform(MockMvcRequestBuilders.post(ADD_BOOKMARK)
                .header("auth", auth2).contentType(MediaType.APPLICATION_JSON)
                .param("auctionId", "1").contentType(MediaType.APPLICATION_JSON));

        mvc.perform(MockMvcRequestBuilders.post(ADD_BOOKMARK)
                .header("auth", auth2).contentType(MediaType.APPLICATION_JSON)
                .param("auctionId", "5").contentType(MediaType.APPLICATION_JSON));


        String response = mvc.perform(MockMvcRequestBuilders.get(HOTTEST)
                .header("auth", auth).contentType(MediaType.APPLICATION_JSON)
                .param("page", "0").param("size", "1"))
                .andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();
        PagedResources<Resource<AuctionDomain>> pagedResource = gson.fromJson(response, PagedResources.class);
        AssertionsForClassTypes.assertThat(pagedResource.getContent().iterator().next().getContent().getId()).isEqualTo(1);

    }

    @Test
    public void search() throws Exception {
        UserDomain userDomain = getFirstUserInfo();
        String response = mvc.perform(MockMvcRequestBuilders.post(SEARCH + "0" + "?page=" + 0 + "&size=" + 40)
                .header("auth", auth).contentType(MediaType.APPLICATION_JSON)
                .param("title", "")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        PagedResources<Resource<AuctionDomain>> auctionDomainResource = gson.fromJson(response, PagedResources.class);
        System.out.println(response);
        //TODO
    }
}

