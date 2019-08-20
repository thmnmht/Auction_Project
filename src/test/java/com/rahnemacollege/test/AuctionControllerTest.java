package com.rahnemacollege.test;


import com.rahnemacollege.domain.AddAuctionDomain;
import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuctionControllerTest extends InitTest{

    private int auctionId = 2;

    @Test
    public void getCategory() throws Exception{
        String response = mvc.perform(MockMvcRequestBuilders.get(CATEGORY).header("auth",auth)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        List<Category> categories = List.of(gson.fromJson(response,Category[].class));
        System.out.println("Categories : ");
        for (Category c :
                categories) {
            System.out.println(c.getCategoryName());
        }
        if(categories.size() < 3)
            fail("Exception not thrown");
    }

    @Test
    public void addAuction() throws Exception{
        String request = createAddAuctionRequest("testADDAuction","",100,5,1,15660254847150L);
        String response = mvc.perform(MockMvcRequestBuilders.post(ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request).header("auth",auth)
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        AuctionDomain auctionDomain = gson.fromJson(response,AuctionDomain.class);
        auctionId = auctionDomain.getId();
        assertThat(auctionDomain.getTitle())
                .isEqualTo("testADDAuction");
        assertThat(auctionDomain.isMine())
                .isEqualTo(true);
    }

    @Test
    public void invalidAddAuction() throws Exception{
        String request = createAddAuctionRequest("invalid base price","",-1,5,1,15660254847150L);
        mvc.perform(MockMvcRequestBuilders.post(ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request).header("auth",auth)
        ).andExpect(status().is(432));
        request = createAddAuctionRequest("invalid max number","",100,1,1,15660254847150L);
        mvc.perform(MockMvcRequestBuilders.post(ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request).header("auth",auth)
        ).andExpect(status().is(434));

        request = createAddAuctionRequest("","",100,5,1,15660254847150L);
        mvc.perform(MockMvcRequestBuilders.post(ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request).header("auth",auth)
        ).andExpect(status().is(430));

        request = createAddAuctionRequest("description too long",LONG_STRING,100,5,1,15660254847150L);
        mvc.perform(MockMvcRequestBuilders.post(ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request).header("auth",auth)
        ).andExpect(status().is(452));

        request = createAddAuctionRequest(LONG_STRING, "",100,5,1,15660254847150L);
        mvc.perform(MockMvcRequestBuilders.post(ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request).header("auth",auth)
        ).andExpect(status().is(451));

        request = createAddAuctionRequest("max number too high","",100,16,1,15660254847150L);
        mvc.perform(MockMvcRequestBuilders.post(ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request).header("auth",auth)
        ).andExpect(status().is(435));

        request = createAddAuctionRequest("invalid category id","",100,5,0,15660254847150L);
        mvc.perform(MockMvcRequestBuilders.post(ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request).header("auth",auth)
        ).andExpect(status().is(436));

        request = createAddAuctionRequest("invalid date","",100,5,1,1566025484715L);
        mvc.perform(MockMvcRequestBuilders.post(ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request).header("auth",auth)
        ).andExpect(status().is(437));

        request = createAddAuctionRequest("invalid date","",100,5,1,1566025484715L);
        mvc.perform(MockMvcRequestBuilders.post(ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request).header("auth",auth)
        ).andExpect(status().is(437));

    }

    @Test
    public void toggleBookmark() throws Exception{
        AuctionDomain auctionDomain = getAuction(auctionId);
        assertThat(auctionDomain.isBookmark())
                .isEqualTo(false);
        mvc.perform(MockMvcRequestBuilders.post(ADD_BOOKMARK + "?auctionId=" + auctionDomain.getId()).header("auth",auth)).andExpect(status().isOk());
        auctionDomain = getAuction(auctionDomain.getId());
        assertThat(auctionDomain.isBookmark())
                .isEqualTo(true);
        mvc.perform(MockMvcRequestBuilders.post(ADD_BOOKMARK + "?auctionId=" + auctionDomain.getId()).header("auth",auth)).andExpect(status().isOk());
        auctionDomain = getAuction(auctionDomain.getId());
        assertThat(auctionDomain.isBookmark())
                .isEqualTo(false);
    }

    /*@Test
    public void addPicture() throws Exception{
        MockMultipartFile profilePicture = new MockMultipartFile("picture", Image_PATH, "text/plain", "some xml".getBytes());
        mvc.perform(MockMvcRequestBuilders.multipart(ADD_PICTURE + auctionId).file(profilePicture).header("auth",auth)).andExpect(status().is(200));
        AuctionDomain auctionDomain = getAuction(auctionId);
        mvc.perform(MockMvcRequestBuilders.get(auctionDomain.getPictures().get(0))).andExpect(status().isOk());
    }*/

    @Test
    public void invalidToggleBookmark() throws Exception{
        mvc.perform(MockMvcRequestBuilders.post(ADD_BOOKMARK + "?auctionId=" + -1)
                .header("auth",auth)).andExpect(status().is(458));
    }

    private AuctionDomain getAuction(int id) throws Exception{
        String response = mvc.perform(MockMvcRequestBuilders.get(FIND + id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("auth",auth)
                ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return gson.fromJson(response,AuctionDomain.class);
    }

    private String createAddAuctionRequest(String title,String description,int base_price,int max_number,int category_id,long date) throws Exception{
        AddAuctionDomain addAuctionDomain = new AddAuctionDomain();
        addAuctionDomain.setTitle(title);
        addAuctionDomain.setBasePrice(base_price);
        addAuctionDomain.setCategoryId(category_id);
        addAuctionDomain.setDate(date);
        addAuctionDomain.setMaxNumber(max_number);
        addAuctionDomain.setDescription(description);
        String request = gson.toJson(addAuctionDomain);
        return request;
    }

}
