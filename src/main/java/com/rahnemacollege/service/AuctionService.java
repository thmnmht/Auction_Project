package com.rahnemacollege.service;


import com.google.common.collect.Lists;
import com.rahnemacollege.domain.AddAuctionDomain;
import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Category;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.AuctionRepository;
import com.rahnemacollege.repository.CategoryRepository;
import com.rahnemacollege.repository.UserRepository;
import com.rahnemacollege.repository.PictureRepository;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuctionService {

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final PictureRepository pictureRepository;

    @Value("${server_ip}")
    private String ip;

    @Autowired
    public AuctionService(UserRepository userRepository, AuctionRepository auctionRepository, CategoryRepository categoryRepository,
                          PictureRepository pictureRepository) {
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.categoryRepository = categoryRepository;
        this.pictureRepository = pictureRepository;
    }

    public Auction addAuction(AddAuctionDomain auctionDomain, User user){
        validation(auctionDomain);
        Auction auction = toAuction(auctionDomain, user);
        auction = auctionRepository.save(auction);
        return auction;
    }

    private void validation(AddAuctionDomain auctionDomain){
        if (auctionDomain.getTitle() == null || auctionDomain.getTitle().length() < 1)
            throw new InvalidInputException(Message.TITLE_NULL);
        if (auctionDomain.getTitle().length() > 50)
            throw new InvalidInputException(Message.TITLE_TOO_LONG);
        if (auctionDomain.getDescription() != null && auctionDomain.getDescription().length() > 1000)
            throw new InvalidInputException(Message.DESCRIPTION_TOO_LONG);
        if (auctionDomain.getDate() < 1)
            throw new InvalidInputException(Message.DATE_NULL);
        if (auctionDomain.getDate() - new Date().getTime() < 1800000L)
            throw new InvalidInputException(Message.DATE_INVALID);
        if (auctionDomain.getBasePrice() < 0)
            throw new InvalidInputException(Message.BASE_PRICE_NULL);
        if (auctionDomain.getMaxNumber() < 2)
            throw new InvalidInputException(Message.MAX_NUMBER_TOO_LOW);
        if (auctionDomain.getMaxNumber() > 15)
            throw new InvalidInputException(Message.MAX_NUMBER_TOO_HIGH);
    }

    private Auction toAuction(AddAuctionDomain auctionDomain, User user) {
        Date date = new Date(auctionDomain.getDate());
        Category category = categoryRepository.findById(auctionDomain.getCategoryId()).orElseThrow(() -> new InvalidInputException(Message.CATEGORY_INVALID));
        Auction auction = new Auction(auctionDomain.getTitle(), auctionDomain.getDescription(), auctionDomain.getBasePrice(), category, date, user, auctionDomain.getMaxNumber());
        return auction;
    }

    public Auction findAuctionById(int id) {
        return auctionRepository.findById(id).orElseThrow(() -> new InvalidInputException(Message.AUCTION_NOT_FOUND));
    }

    public AuctionDomain toAuctionDomain(Auction auction, User user) {
        AuctionDomain auctionDomain = new AuctionDomain(auction.getTitle(), auction.getDescription(), auction.getBase_price(), auction.getDate().getTime(), auction.getCategory().getId(), auction.getMax_number());
        auctionDomain.setId(auction.getId());
        if (auction.getOwner().getId() == user.getId())
            auctionDomain.setMine(true);
        String userEmail = user.getEmail();
        user = userRepository.findByEmail(userEmail).get();
        if(user.getBookmarks().contains(auction)) {
            auctionDomain.setBookmark(true);
        }
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

//    public Page<AuctionDomain> filter(int categoryId, int page, int size) {
//        List<Auction> auctions = getAllAliveAuctions().stream().filter(c -> c.getCategory().getId() == categoryId).collect(Collectors.toList());
//        return toPage(auctions, page, size);
//    }

    public List<Auction> getAllAliveAuctions() {
        return Lists.newArrayList(auctionRepository.findAll()).stream().filter(auction -> auction.getState() == 0).sorted((a,b) -> b.getId() - a.getId())
                .collect(Collectors.toList());
    }

    public List<Auction> getAll() {
        return new ArrayList<>(Lists.newArrayList(auctionRepository.findAll()));
    }

    public Auction findById(int id) {
        Auction auction = auctionRepository.findById(id).orElseThrow(() -> new InvalidInputException(Message.AUCTION_NOT_FOUND));
        return auction;
    }

    public List<Auction> findByTitle(String title, int category_id) {
        List<Auction> auctions = new ArrayList<>();
        if (category_id == 0)
            auctions = getAllAliveAuctions();
        else {
            List<Auction> tmp = getAllAliveAuctions().stream().filter(c -> c.getCategory().getId() == category_id).collect(Collectors.toList());
            auctions.addAll(tmp);
        }
        auctions = auctions.stream()
                .filter(a -> a.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
        return auctions;
    }

    public List<Auction> findByOwner(User user) {
        List<Auction> auctions = auctionRepository.findByOwner_id(user.getId());
        return auctions;
    }

    public List<AuctionDomain> toAuctionDomainList(List<Auction> auctions, User user) {
        return Lists.newArrayList(auctions.stream()
                .map(a -> toAuctionDomain(a, user))
                .collect(Collectors.toList()));
    }


//    public Page<AuctionDomain> getAllAuctions(int page, int size) {
//        return toPage(getAll(), page, size);
//    }

    public Page<AuctionDomain> toPage(List<AuctionDomain> list, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        Page<AuctionDomain> pages = new PageImpl(list.subList(start, end), pageable, list.size());
        return pages;
    }

    public Page<Auction> getHottest(PageRequest request) {
        return auctionRepository.findHottest(request);
    }

    public Page<AuctionDomain> toAuctionDomainPage(Page<Auction> auctionPage, User user) {
        List<AuctionDomain> auctionDomainList = new ArrayList<>();
        auctionPage.forEach(auction -> auctionDomainList.add(toAuctionDomain(auction, user)));
        return new PageImpl<>(auctionDomainList);
    }


    @Transactional
    public void addBookmark(User user, int id) {
        user = userRepository.findByEmail(user.getEmail()).get();
        Set<Auction> bookmarks = user.getBookmarks();
        Auction newBookmark = auctionRepository.findById(id).orElseThrow(() -> new InvalidInputException(Message.AUCTION_NOT_FOUND));
        if(bookmarks.contains(newBookmark))
            bookmarks.remove(newBookmark);
        else
            bookmarks.add(newBookmark);
        user.setBookmarks(bookmarks);
        userRepository.save(user);
    }

}
