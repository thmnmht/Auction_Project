package com.rahnemacollege.controller;


import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Category;
import com.rahnemacollege.service.AuctionService;
import com.rahnemacollege.util.ResourceAssembler;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final ResourceAssembler assembler;

    public AuctionController(AuctionService auctionService, ResourceAssembler assembler) {
        this.auctionService = auctionService;
        this.assembler = assembler;
    }

    @GetMapping("/category")
    public List<Category> getCategory(){
        return auctionService.getCategory();
    }


    //no date!!!
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Resource<AuctionDomain> add(@RequestPart String title,
                                 String description,
                                 int base_price,
                                 long date,
            int category_id,int max_number, @RequestPart MultipartFile[] images) throws IOException {
        AuctionDomain auctionDomain = new AuctionDomain(title,description,base_price,date,category_id,max_number);
        return assembler.toResource(auctionService.addAuction(auctionDomain,images));
    }

    @GetMapping("/greeting")
    public String greeting() {
        return "hello world :)";
    }

    @GetMapping("/find/{id}")
    public Resource<AuctionDomain> one(@PathVariable int id) {
        return assembler.toResource(auctionService.findById(id));
    }

    @GetMapping("/image/{id}")
    public void showPic(){}

    @GetMapping("/all")
    public Resources<Resource<AuctionDomain>> all() {

       // System.out.println(new ClassPathResource("image/sid.jpg").toString());

        return assembler.toResourcesAuc(auctionService.getAll());
    }

    @GetMapping("/search/{title}")
    public Resources<Resource<AuctionDomain>> search(@PathVariable String title){
        List<AuctionDomain> auctions = auctionService.findByTitle(title);
        return assembler.toResourcesAuc(auctions);
    }

    @GetMapping("/filter/{category_id}")
    public Resources<Resource<AuctionDomain>> filter(@PathVariable int category_id){
        return assembler.toResourcesAuc(auctionService.filter(category_id));
    }
}


