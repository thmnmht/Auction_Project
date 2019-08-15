package com.rahnemacollege.controller;

import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.service.AuctionService;
import com.rahnemacollege.util.ResourceAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;

@RestController
@RequestMapping("/home")
public class HomePageController {

    private final AuctionService auctionService;
    private final ResourceAssembler assembler;
    private final Logger log;


    public HomePageController(AuctionService auctionService, ResourceAssembler assembler) {
        this.auctionService = auctionService;
        this.assembler = assembler;
        log = LoggerFactory.getLogger(AuctionController.class);
    }

    @GetMapping("/hottest")
    PagedResources<Resource<AuctionDomain>> paged(@RequestParam("page") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size,
                                                  PagedResourcesAssembler<AuctionDomain> assembler,
                                                  HttpServletRequest request) {
        log.info("get hottest");
        String appUrl = request.getScheme() + "://" + request.getServerName();
        Page<AuctionDomain> hotAuctions = auctionService.getHottest(PageRequest.of(page, size),appUrl);
        return assembler.toResource(hotAuctions);
    }


    @PostMapping("/search/{category}")
    public PagedResources<Resource<AuctionDomain>> search(@PathParam("title") String title,
                                                          @PathVariable int category,
                                                          @RequestParam("page") int page,
                                                          @RequestParam("size") int size,
                                                          HttpServletRequest request,
                                                          PagedResourcesAssembler<AuctionDomain> assembler) {
        log.info("search");
        String appUrl = request.getScheme() + "://" + request.getServerName();
        return assembler.toResource(auctionService.findByTitle(title, category, page, size,appUrl));
    }

    @GetMapping("/filter/{category_id}")
    public Resources<Resource<AuctionDomain>> filter(@PathVariable int category_id, @RequestParam("page") int page, @RequestParam("size") int size, PagedResourcesAssembler<AuctionDomain> assembler,
                                                     HttpServletRequest request) {
        log.info("filter");
        String appUrl = request.getScheme() + "://" + request.getServerName();
        return assembler.toResource(auctionService.filter(category_id, page, size,appUrl));
    }

    @GetMapping("/all")
    public PagedResources<Resource<AuctionDomain>> getPage(@RequestParam("page") int page, @RequestParam("size") int size, PagedResourcesAssembler<AuctionDomain> assembler,HttpServletRequest request) {
        log.info("get all auctions");
        String appUrl = request.getScheme() + "://" + request.getServerName();
        Page<AuctionDomain> personPage = auctionService.getAllAuctions(page, size,appUrl);
        return assembler.toResource(personPage);
    }

}
