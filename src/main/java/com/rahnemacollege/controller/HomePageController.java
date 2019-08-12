package com.rahnemacollege.controller;

import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.service.AuctionService;
import com.rahnemacollege.util.ResourceAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/home")
public class HomePageController {

    private final AuctionService auctionService;
    private final ResourceAssembler assembler;


    public HomePageController(AuctionService auctionService, ResourceAssembler assembler) {
        this.auctionService = auctionService;
        this.assembler = assembler;
    }

    @GetMapping("/hottest")
    PagedResources<Resource<AuctionDomain>> paged(@RequestParam("page") int page,
                                                  @RequestParam(value="size", defaultValue="10") int size,
                                                  PagedResourcesAssembler<AuctionDomain> assembler) {
        Page<AuctionDomain> hotAuctions = auctionService.getHottest(PageRequest.of(page, size));
        return assembler.toResource(hotAuctions);
    }



    @GetMapping("/search/{title}")
    public PagedResources<Resource<AuctionDomain>> search(@PathVariable String title,@RequestParam("page") int page, @RequestParam("size") int size, PagedResourcesAssembler<AuctionDomain> assembler){
        return assembler.toResource(auctionService.findByTitle(title,page,size));
    }

    @GetMapping("/filter/{category_id}")
    public Resources<Resource<AuctionDomain>> filter(@PathVariable int category_id,@RequestParam("page") int page, @RequestParam("size") int size, PagedResourcesAssembler<AuctionDomain> assembler){
        return assembler.toResource(auctionService.filter(category_id,page,size));
    }

    @GetMapping("/all")
    public PagedResources<Resource<AuctionDomain>> getPage(@RequestParam("page") int page, @RequestParam("size") int size, PagedResourcesAssembler<AuctionDomain> assembler){
        Page<AuctionDomain> personPage = auctionService.getAllAuctions(page, size);
        return assembler.toResource(personPage);
    }

}
