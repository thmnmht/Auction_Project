package com.rahnemacollege.service;


import com.google.common.collect.Lists;
import com.rahnemacollege.controller.AuctionController;
import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Category;
import com.rahnemacollege.model.Picture;
import com.rahnemacollege.repository.AuctionRepository;
import com.rahnemacollege.repository.CategoryRepository;
import com.rahnemacollege.repository.PictureRepository;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import com.rahnemacollege.util.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final PictureService pictureService;
    private final Validator validator;

    @Value("${server_ip}")
    private String ip;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, CategoryRepository categoryRepository,
                          UserDetailsServiceImpl userDetailsService, PictureService pictureService, Validator validator) {
        this.auctionRepository = auctionRepository;
        this.categoryRepository = categoryRepository;
        this.userDetailsService = userDetailsService;
        this.pictureService = pictureService;
        this.validator = validator;
    }


    public AuctionDomain addAuction(AuctionDomain auctionDomain, MultipartFile[] images,String url) throws IOException {
        validation(auctionDomain);
        Auction auction = toAuction(auctionDomain);
        auctionRepository.save(auction);
        if (images != null)
            savePictures(auction, images,url);
        return toAuctionDomain(auction,url);
    }

    private void validation(AuctionDomain auctionDomain) {
        validator.validTitle(auctionDomain.getTitle());
        validator.validDescription(auctionDomain.getDescription());
        validator.validDate(auctionDomain.getDate());
        validator.validPrice(auctionDomain.getBase_price());
        validator.validMaxNumber(auctionDomain.getMax_number());
    }

    private void savePictures(Auction auction, MultipartFile[] images,String url) throws IOException {
        new File("./images/auction_images/" + auction.getId() + "/").mkdirs();
        for (MultipartFile image :
                images) {
            String fileName = new Date().getTime() + ".jpg";
            String pathName = "./images/auction_images/" + auction.getId() + "/" + fileName;
            pictureService.save(image, pathName, auction);
        }
    }

    public Auction toAuction(AuctionDomain auctionDomain) {
        Date date = new Date(auctionDomain.getDate());
        Category category = categoryRepository.findById(auctionDomain.getCategory_id()).orElseThrow(() -> new InvalidInputException(Message.CATEGORY_INVALID));
        Auction auction = new Auction(auctionDomain.getTitle(), auctionDomain.getDescription(), auctionDomain.getBase_price(), category, date, userDetailsService.getUser(), auctionDomain.getMax_number());
        return auction;
    }

    public AuctionDomain toAuctionDomain(Auction auction,String url) {
        String u = "http://" + ip;
                AuctionDomain auctionDomain = new AuctionDomain(auction.getTitle(), auction.getDescription(), auction.getBase_price(), auction.getDate().getTime(), auction.getCategory().getId(), auction.getMax_number());
        auctionDomain.setId(auction.getId());
        if(auction.getOwner().getId() == userDetailsService.getUser().getId())
            auctionDomain.setMine(true);
        List<String> auctionPictures = pictureService.getAll().stream().filter(picture ->
                picture.getFileName().contains("/" + auction.getId() + "/")).map(
                picture -> u + picture.getFileName()
        ).collect(Collectors.toList());
        auctionDomain.setPictures(auctionPictures);

        return auctionDomain;
    }

    public List<Category> getCategory() {
        return Lists.newArrayList(categoryRepository.findAll());

    }

    public Page<AuctionDomain> filter(int category_id, int page, int size,String url) {
        List<AuctionDomain> auctions = getAll(url).stream().filter(c -> c.getCategory_id() == category_id).collect(Collectors.toList());
        return toPage(auctions, page, size);
    }

    public List<AuctionDomain> getAll(String url) {
        return Lists.newArrayList(auctionRepository.findAll()).stream().filter(auction -> auction.getState() == 0)
                .map(a -> toAuctionDomain(a,url))
                .collect(Collectors.toList());
    }

    public Auction findById(int id) {
        Auction auction = auctionRepository.findById(id).orElseThrow(() -> new InvalidInputException(Message.AUCTION_NOT_FOUND));
        return auction;
    }

    public Page<AuctionDomain> findByTitle(String title, int category_id, int page, int size,String url) {
        List<AuctionDomain> auctions = new ArrayList<>();
        if (category_id == 0)
            auctions = getAll(url);
        else {
            List<AuctionDomain> tmp = getAll(url).stream().filter(c -> c.getCategory_id() == category_id).collect(Collectors.toList());
            auctions.addAll(tmp);
        }
        auctions = auctions.stream()
                .filter(a -> a.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
        return toPage(auctions, page, size);
    }

    public Page<AuctionDomain> getAllAuctions(int page, int size,String url) {
        return toPage(getAll(url), page, size);
    }

    private Page<AuctionDomain> toPage(List<AuctionDomain> list, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        Page<AuctionDomain> pages = new PageImpl(list.subList(start, end), pageable, list.size());
        return pages;
    }


    public Page<AuctionDomain> getHottest(PageRequest request,String url) {
        return toAuctionDomainPage(auctionRepository.findHottest(request),url);
    }

    private Page<AuctionDomain> toAuctionDomainPage(Page<Auction> auctionPage,String url) {
        List<AuctionDomain> auctionDomainList = new ArrayList<>();
        auctionPage.forEach(auction -> auctionDomainList.add(toAuctionDomain(auction,url)));
        return new PageImpl<>(auctionDomainList);
    }

}
