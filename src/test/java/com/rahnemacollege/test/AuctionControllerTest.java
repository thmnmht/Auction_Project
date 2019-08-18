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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
        AuctionDomain auctionDomain = addAuction("testADDAuction","",100,5,1,15660254847150L);
        assertThat(auctionDomain.getTitle())
                .isEqualTo("testADDAuction");
        assertThat(auctionDomain.isMine())
                .isEqualTo(true);
    }

    @Test
    public void toggleBookMark() throws Exception{
        AuctionDomain auctionDomain = addAuction("test bookmark","",100,5,1,15660254847150L);
        assertThat(auctionDomain.isBookmark())
                .isEqualTo(false);
        mvc.perform(MockMvcRequestBuilders.post(ADD_BOOKMARK + "?auctionId=" + auctionDomain.getId()).header("auth",auth));
    }

    private AuctionDomain addAuction(String title,String description,int base_price,int max_number,int category_id,long date) throws Exception{
        AddAuctionDomain addAuctionDomain = new AddAuctionDomain();
        addAuctionDomain.setTitle(title);
        addAuctionDomain.setBasePrice(base_price);
        addAuctionDomain.setCategoryId(category_id);
        addAuctionDomain.setDate(date);
        addAuctionDomain.setMaxNumber(max_number);
        addAuctionDomain.setDescription(description);
        String request = gson.toJson(addAuctionDomain);
        String response = mvc.perform(MockMvcRequestBuilders.post(ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request).header("auth",auth)
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        AuctionDomain auctionDomain = gson.fromJson(response,AuctionDomain.class);
        return auctionDomain;
    }

}
