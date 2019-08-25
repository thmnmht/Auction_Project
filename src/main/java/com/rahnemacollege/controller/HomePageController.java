package com.rahnemacollege.controller;

import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.AuctionService;
import com.rahnemacollege.service.BidService;
import com.rahnemacollege.service.UserDetailsServiceImpl;
import com.rahnemacollege.util.ResourceAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/home")
public class HomePageController {

    private final AuctionService auctionService;
    private final ResourceAssembler assembler;
    private final Logger log;
    private final UserDetailsServiceImpl userDetailsService;
    private final BidService bidService;



    public HomePageController(AuctionService auctionService, ResourceAssembler assembler, UserDetailsServiceImpl userDetailsService, BidService bidService) {
        this.auctionService = auctionService;
        this.assembler = assembler;
        this.userDetailsService = userDetailsService;
        this.bidService = bidService;
        log = LoggerFactory.getLogger(AuctionController.class);
    }

    @GetMapping("/hottest")
    PagedResources<Resource<AuctionDomain>> paged(@RequestParam("page") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size,
                                                  PagedResourcesAssembler<AuctionDomain> assembler) {
        log.info("get hottest");
        User user = userDetailsService.getUser();
        Page<Auction> hotAuctions = auctionService.getHottest(PageRequest.of(page, size));
        List<AuctionDomain> auctionDomainList = hotAuctions.stream().map(a ->
            auctionService.toAuctionDomain(a,user,bidService.getMembers(a))).collect(Collectors.toList());
        Page<AuctionDomain> tmp =  new PageImpl<>(auctionDomainList);
        return assembler.toResource(tmp);
    }


    @PostMapping("/search/{category}")
    public PagedResources<Resource<AuctionDomain>> search(@PathParam("title") String title,
                                                          @PathVariable int category,
                                                          @RequestParam("page") int page,
                                                          @RequestParam("size") int size,
                                                          PagedResourcesAssembler<AuctionDomain> assembler) {
        log.info("search");
        List<Auction> auctions = auctionService.findByTitle(title, category);
        User user = userDetailsService.getUser();
        List<AuctionDomain> auctionDomains = auctions.stream().map(a ->
                auctionService.toAuctionDomain(a,user,bidService.getMembers(a))).collect(Collectors.toList());
        return assembler.toResource(auctionService.toPage(auctionDomains, page, size));
    }

//    @GetMapping("/filter/{categoryId}")
//    public Resources<Resource<AuctionDomain>> filter(@PathVariable int categoryId, @RequestParam("page") int page, @RequestParam("size") int size, PagedResourcesAssembler<AuctionDomain> assembler) {
//        log.info("filter");
//        return assembler.toResource(auctionService.filter(categoryId, page, size));
//    }

//    @GetMapping("/all")
//    public PagedResources<Resource<AuctionDomain>> getPage(@RequestParam("page") int page, @RequestParam("size") int size, PagedResourcesAssembler<AuctionDomain> assembler) {
//        log.info("get all auctions");
//        Page<AuctionDomain> personPage = auctionService.getAllAuctions(page, size);
//        return assembler.toResource(personPage);
//    }

}
