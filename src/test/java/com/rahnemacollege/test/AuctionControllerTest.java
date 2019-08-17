package com.rahnemacollege.test;


import com.rahnemacollege.domain.AddAuctionDomain;
import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuctionControllerTest extends InitTest{

    @Test
    public void getCategory() throws Exception{
        String response = mvc.perform(MockMvcRequestBuilders.get(CATEGORY).header("auth",auth)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Category[] categories = gson.fromJson(response,Category[].class);
        System.out.println("Categories : ");
        for (Category c :
                categories) {
            System.out.println(c.getCategoryName());
        }
    }

    @Test
    public void addAuction() throws Exception{
        AddAuctionDomain addAuctionDomain = new AddAuctionDomain();
        addAuctionDomain.setTitle("testADDAuction");
        addAuctionDomain.setBase_price(1000);
        addAuctionDomain.setCategory_id(1);
        addAuctionDomain.setDate(15660254847150L);
        addAuctionDomain.setMax_number(5);
        addAuctionDomain.setDescription("");
        String request = gson.toJson(addAuctionDomain);
        String response = mvc.perform(MockMvcRequestBuilders.post(ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request).header("auth",auth)
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        AuctionDomain auctionDomain = gson.fromJson(response,AuctionDomain.class);
        System.out.println(auctionDomain.getTitle());
    }

}
