package com.rahnemacollege.controller;

import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.User;
import com.rahnemacollege.service.AuctionService;
import com.rahnemacollege.service.BidService;
import com.rahnemacollege.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/home")
public class HomePageController {

    private final AuctionService auctionService;
    private final Logger log;
    private final UserDetailsServiceImpl userDetailsService;
    private final BidService bidService;


    public HomePageController(AuctionService auctionService, UserDetailsServiceImpl userDetailsService, BidService bidService) {
        this.auctionService = auctionService;
        this.userDetailsService = userDetailsService;
        this.bidService = bidService;
        log = LoggerFactory.getLogger(AuctionController.class);
    }

//    @GetMapping("/hottest")
//    PagedResources<Resource<AuctionDomain>> paged(@RequestParam("page") int page,
//                                                  @RequestParam(value = "size") int size,
//                                                  PagedResourcesAssembler<AuctionDomain> assembler) {
//        log.info("get hottest");
//        User user = userDetailsService.getUser();
//        List<Auction> hotAuctions = auctionService.getHottest();
//        List<AuctionDomain> auctionDomainList = hotAuctions.stream().map(a ->
//                auctionService.toAuctionDomain(a, user, bidService.getMembers(a))).collect(Collectors.toList());
//        return assembler.toResource(auctionService.toPage(auctionDomainList, page, size));
//    }


    @PostMapping("/search/{category}")
    public PagedResources<Resource<AuctionDomain>> search(@RequestParam("title") String title,
                                                          @PathVariable int category,
                                                          @RequestParam(value = "hottest", defaultValue = "false") boolean hottest,
                                                          @RequestParam("page") int page,
                                                          @RequestParam("size") int size,
                                                          PagedResourcesAssembler<AuctionDomain> assembler) {
        log.info("search");
        List<Auction> auctions = auctionService.findByTitle(title, category, hottest);
        User user = userDetailsService.getUser();
        List<AuctionDomain> auctionDomains = auctions.stream().map(a ->
                auctionService.toAuctionDomain(a, user, bidService.getMembers(a))).collect(Collectors.toList());
        log.info("user with email " + user.getEmail() + " search for title " + title);
        return assembler.toResource(auctionService.toPage(auctionDomains, page, size));
    }

}
