package com.rahnemacollege.controller;

import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.service.AuctionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomePageController {
    private final AuctionService service;

    public HomePageController(AuctionService service) {
        this.service = service;
    }

    @GetMapping("/hottest")
    PagedResources<Resource<AuctionDomain>> paged(@RequestParam("page") int page,
                                                  @RequestParam(value="size", defaultValue="10") int size,
                                                  PagedResourcesAssembler<AuctionDomain> assembler) {
        Page<AuctionDomain> hotAuctions = service.getHottest(PageRequest.of(page, size));
        return assembler.toResource(hotAuctions);
    }

    @GetMapping("/search")
    PagedResources<Resource<AuctionDomain>> search(@RequestParam("title") String title,
                                                   @RequestParam("page") int page,
                                                   @RequestParam(value="size", defaultValue="10") int size,
                                                   PagedResourcesAssembler<AuctionDomain> assembler) {
        Page<AuctionDomain> foundAuctions = service.findByTitle(title,PageRequest.of(page, size));
        return assembler.toResource(foundAuctions);
    }

    @GetMapping("/search")
    PagedResources<Resource<AuctionDomain>> find(@RequestParam("category") String categoryName,
                                                   @RequestParam("page") int page,
                                                   @RequestParam(value="size", defaultValue="10") int size,
                                                   PagedResourcesAssembler<AuctionDomain> assembler) {
        Page<AuctionDomain> foundAuctions = service.findByCategory(categoryName,PageRequest.of(page, size));
        return assembler.toResource(foundAuctions);
    }

}
