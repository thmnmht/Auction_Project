package com.rahnemacollege.service;


import com.google.common.collect.Lists;
import com.rahnemacollege.domain.AddAuctionDomain;
import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Category;
import com.rahnemacollege.model.Picture;
import com.rahnemacollege.model.User;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.AuctionRepository;
import com.rahnemacollege.repository.CategoryRepository;
import com.rahnemacollege.repository.UserRepository;
import com.rahnemacollege.repository.PictureRepository;
import com.rahnemacollege.repository.UserRepository;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuctionService {

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final PictureRepository pictureRepository;

    @Value("${server_ip}")
    private String ip;

    @Autowired
    public AuctionService(UserRepository userRepository, AuctionRepository auctionRepository, CategoryRepository categoryRepository,
                          UserDetailsServiceImpl userDetailsService,
                          PictureRepository pictureRepository) {
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.categoryRepository = categoryRepository;
        this.userDetailsService = userDetailsService;
        this.pictureRepository = pictureRepository;
    }

    public Auction addAuction(AddAuctionDomain auctionDomain){
        validation(auctionDomain);
        Auction auction = toAuction(auctionDomain);
        auctionRepository.save(auction);
        return auction;
    }

    private void validation(AddAuctionDomain auctionDomain){
        if (auctionDomain.getTitle() == null || auctionDomain.getTitle().length() < 1)
            throw new InvalidInputException(Message.TITLE_NULL);
        if (auctionDomain.getTitle().length() > 50)
            throw new InvalidInputException(Message.TITLE_TOO_LONG);
        if (auctionDomain.getDescription().length() > 1000)
            throw new InvalidInputException(Message.DESCRIPTION_TOO_LONG);
        if (auctionDomain.getDate() < 1)
            throw new InvalidInputException(Message.DATE_NULL);
        if (auctionDomain.getDate() - new Date().getTime() < 1800000L)
            throw new InvalidInputException(Message.DATE_INVALID);
        if (auctionDomain.getBase_price() < 0)
            throw new InvalidInputException(Message.BASE_PRICE_NULL);
        if (auctionDomain.getMax_number() < 2)
            throw new InvalidInputException(Message.MAX_NUMBER_TOO_LOW);
        if (auctionDomain.getMax_number() > 15)
            throw new InvalidInputException(Message.MAX_NUMBER_TOO_HIGH);
    }

    private Auction toAuction(AddAuctionDomain auctionDomain) {
        Date date = new Date(auctionDomain.getDate());
        Category category = categoryRepository.findById(auctionDomain.getCategory_id()).orElseThrow(() -> new InvalidInputException(Message.CATEGORY_INVALID));
        Auction auction = new Auction(auctionDomain.getTitle(), auctionDomain.getDescription(), auctionDomain.getBase_price(), category, date, userDetailsService.getUser(), auctionDomain.getMax_number());
        return auction;
    }

    public Auction findAuctionById(int id) {
        return auctionRepository.findById(id).orElseThrow(() -> new InvalidInputException(Message.AUCTION_NOT_FOUND));
    }

    public AuctionDomain toAuctionDomain(Auction auction) {
        AuctionDomain auctionDomain = new AuctionDomain(auction.getTitle(), auction.getDescription(), auction.getBase_price(), auction.getDate().getTime(), auction.getCategory().getId(), auction.getMax_number());
        auctionDomain.setId(auction.getId());
        if (auction.getOwner().getId() == userDetailsService.getUser().getId())
            auctionDomain.setMine(true);
        List<String> auctionPictures = Lists.newArrayList(pictureRepository.findAll()).stream().filter(picture ->
                picture.getFileName().contains("/" + auction.getId() + "/")).map(
                picture -> "http://" + ip + picture.getFileName()
        ).collect(Collectors.toList());
        auctionDomain.setPictures(auctionPictures);
        return auctionDomain;
    }

    public List<Category> getCategory() {
        return Lists.newArrayList(categoryRepository.findAll());

    }

    public Page<AuctionDomain> filter(int category_id, int page, int size) {
        List<AuctionDomain> auctions = getAllAliveAuctions().stream().filter(c -> c.getCategory_id() == category_id).collect(Collectors.toList());
        return toPage(auctions, page, size);
    }

    public List<AuctionDomain> getAllAliveAuctions() {
        return Lists.newArrayList(auctionRepository.findAll()).stream().filter(auction -> auction.getState() == 0).sorted((a,b) -> b.getId() - a.getId())
                .map(a -> toAuctionDomain(a))
                .collect(Collectors.toList());
    }

    public List<AuctionDomain> getAll() {
        return Lists.newArrayList(auctionRepository.findAll()).stream()
                .map(a -> toAuctionDomain(a))
                .collect(Collectors.toList());
    }

    public Auction findById(int id) {
        Auction auction = auctionRepository.findById(id).orElseThrow(() -> new InvalidInputException(Message.AUCTION_NOT_FOUND));
        return auction;
    }

    public Page<AuctionDomain> findByTitle(String title, int category_id, int page, int size) {
        List<AuctionDomain> auctions = new ArrayList<>();
        if (category_id == 0)
            auctions = getAllAliveAuctions();
        else {
            List<AuctionDomain> tmp = getAllAliveAuctions().stream().filter(c -> c.getCategory_id() == category_id).collect(Collectors.toList());
            auctions.addAll(tmp);
        }
        auctions = auctions.stream()
                .filter(a -> a.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
        return toPage(auctions, page, size);
    }

    public Page<AuctionDomain> findByOwner(User user, int page, int size) {
        List<AuctionDomain> auctions = toAuctionDomainList(auctionRepository.findByOwner_id(user.getId()));
        return toPage(auctions, page, size);
    }

    public List<AuctionDomain> toAuctionDomainList(List<Auction> auctions) {
        return Lists.newArrayList(auctions.stream()
                .map(this::toAuctionDomain)
                .collect(Collectors.toList()));
    }


    public Page<AuctionDomain> getAllAuctions(int page, int size) {
        return toPage(getAll(), page, size);
    }

    public Page<AuctionDomain> toPage(List<AuctionDomain> list, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        Page<AuctionDomain> pages = new PageImpl(list.subList(start, end), pageable, list.size());
        return pages;
    }

    public Page<AuctionDomain> getHottest(PageRequest request) {
        return toAuctionDomainPage(auctionRepository.findHottest(request));
    }

    private Page<AuctionDomain> toAuctionDomainPage(Page<Auction> auctionPage) {
        List<AuctionDomain> auctionDomainList = new ArrayList<>();
        auctionPage.forEach(auction -> auctionDomainList.add(toAuctionDomain(auction)));
        return new PageImpl<>(auctionDomainList);
    }


    @Transactional
    public void addBookmark(User user, int id) {
        user = userRepository.findByEmail(user.getEmail()).get();
        Set<Auction> bookmarks = user.getBookmarks();
        Auction newBookmark = auctionRepository.findById(id).orElseThrow(() -> new InvalidInputException(Message.AUCTION_NOT_FOUND));
        bookmarks.add(newBookmark);
        user.setBookmarks(bookmarks);
        userRepository.save(user);
    }

}
