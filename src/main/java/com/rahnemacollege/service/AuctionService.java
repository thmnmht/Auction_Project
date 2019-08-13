package com.rahnemacollege.service;


import com.google.common.collect.Lists;
import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Category;
import com.rahnemacollege.model.Picture;
import com.rahnemacollege.repository.AuctionRepository;
import com.rahnemacollege.repository.CategoryRepository;
import com.rahnemacollege.util.exceptions.InvalidInputException;
import com.rahnemacollege.util.exceptions.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final PictureService pictureService;
    private final Validator validator;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, CategoryRepository categoryRepository,
                          UserDetailsServiceImpl userDetailsService, PictureService pictureService, Validator validator) {
        this.auctionRepository = auctionRepository;
        this.categoryRepository = categoryRepository;
        this.userDetailsService = userDetailsService;
        this.pictureService = pictureService;
        this.validator = validator;
    }


    public AuctionDomain addAuction(AuctionDomain auctionDomain, MultipartFile[] images) throws IOException {
        validation(auctionDomain);
        Auction auction = toAuction(auctionDomain);
        auctionRepository.save(auction);
        if (images != null)
            savePictures(auction, images);
        return toAuctionDomain(auction);
    }

    private void validation(AuctionDomain auctionDomain) {
        validator.validTitle(auctionDomain.getTitle());
        validator.validDescription(auctionDomain.getDescription());
        validator.validDate(auctionDomain.getDate());
        validator.validPrice(auctionDomain.getBase_price());
        validator.validMaxNumber(auctionDomain.getMax_number());
    }

    private void savePictures(Auction auction, MultipartFile[] images) throws IOException {
        String path = "C:/ActionProjectImages/ActionImages/" + auction.getId() + "/";
        new File(path).mkdirs();
        for (MultipartFile image :
                images) {
            String fileName = new Date().getTime() + ".jpg";
            String pathName = path + fileName;
            pictureService.save(image, pathName, auction);
        }
    }

    public Auction toAuction(AuctionDomain auctionDomain) {
        Date date = new Date(auctionDomain.getDate());
        Category category = categoryRepository.findById(auctionDomain.getCategory_id()).orElseThrow(() -> new InvalidInputException(Message.CATEGORY_INVALID));
        Auction auction = new Auction(auctionDomain.getTitle(), auctionDomain.getDescription(), auctionDomain.getBase_price(), category, date, userDetailsService.getUser(), auctionDomain.getMax_number());
        return auction;
    }

    public AuctionDomain toAuctionDomain(Auction auction) {
        AuctionDomain auctionDomain = new AuctionDomain(auction.getTitle(), auction.getDescription(), auction.getBase_price(), auction.getDate().getTime(), auction.getCategory().getId(), auction.getMax_number());
        auctionDomain.setId(auction.getId());
        List<String> auctionPictures = pictureService.getAll().stream().filter(picture ->
                picture.getFileName().startsWith("C:/ActionProjectImages/ActionImages/" + auction.getId() + "/")).map(
                Picture::getFileName
        ).collect(Collectors.toList());
        auctionDomain.setPictures(auctionPictures);

        return auctionDomain;
    }

    public List<Category> getCategory() {
        return Lists.newArrayList(categoryRepository.findAll());
    }

    public Page<AuctionDomain> filter(int[] categories_id, int page, int size) {
        List<AuctionDomain> auctions = new ArrayList<>();
        for (int category_id : categories_id) {
            auctions.addAll(getAll().stream().filter(c -> c.getCategory_id() == category_id).collect(Collectors.toList()));
        }
        return toPage(auctions, page, size);
    }

    public List<AuctionDomain> getAll() {
        return Lists.newArrayList(auctionRepository.findAll()).stream()
                .map(this::toAuctionDomain)
                .collect(Collectors.toList());
    }

    public AuctionDomain findById(int id) {
        Auction auction = auctionRepository.findById(id).orElseThrow(() -> new InvalidInputException(Message.AUCTION_NOT_FOUND));
        return toAuctionDomain(auction);
    }

    public Auction findAuctionById(int id) {
        return auctionRepository.findById(id).orElseThrow(() -> new InvalidInputException(Message.AUCTION_NOT_FOUND));
    }

    public Page<AuctionDomain> findByTitle(String title, int[] categories_id, int page, int size) {
        List<AuctionDomain> auctions = new ArrayList<>();
        if (categories_id == null || categories_id.length < 1)
            auctions = getAll();
        else
            for (int category_id :
                    categories_id) {
                List<AuctionDomain> tmp = getAll().stream().filter(c -> c.getCategory_id() == category_id).collect(Collectors.toList());
                auctions.addAll(tmp);
            }
        auctions = auctions.stream()
                .filter(a -> a.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
        return toPage(auctions, page, size);
    }

    public Page<AuctionDomain> getAllAuctions(int page, int size) {
        return toPage(getAll(), page, size);
    }

    private Page<AuctionDomain> toPage(List<AuctionDomain> list, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        Page<AuctionDomain> pages = new PageImpl(list.subList(start, end), pageable, list.size());
        return pages;
    }

    //TODO : change exception handling!
    //TODO : remove it!
    public Resource imageUpload(int id, String fileName) {
        String path = "./images/auction_images/" + id + "/" + fileName;
        Path filePath = Paths.get(path).toAbsolutePath().normalize();
        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return resource;
    }


    public Page<AuctionDomain> getHottest(PageRequest request) {
        return toAuctionDomainPage(auctionRepository.findHottest(request));
    }

    private Page<AuctionDomain> toAuctionDomainPage(Page<Auction> auctionPage) {
        List<AuctionDomain> auctionDomainList = new ArrayList<>();
        auctionPage.forEach(auction -> auctionDomainList.add(toAuctionDomain(auction)));
        return new PageImpl<>(auctionDomainList);
    }

}
