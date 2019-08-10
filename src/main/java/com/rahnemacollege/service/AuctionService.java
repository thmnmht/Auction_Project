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
import com.rahnemacollege.util.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final PictureRepository pictureRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, CategoryRepository categoryRepository,
                          PictureRepository pictureRepository, UserDetailsServiceImpl userDetailsService) {
        this.auctionRepository = auctionRepository;
        this.categoryRepository = categoryRepository;
        this.pictureRepository = pictureRepository;
        this.userDetailsService = userDetailsService;
    }


    public AuctionDomain addAuction(AuctionDomain auctionDomain,MultipartFile[] images) throws IOException {
        Auction auction = toAuction(auctionDomain);
        auctionRepository.save(auction);
        if(images != null)
            savePictures(auction,images);
        return toAuctionDomain(auction);
    }


     public void savePictures(Auction auction,MultipartFile[] images) throws IOException {
         new File("./images/auction_images/" + auction.getId() + "/" ).mkdirs();
         for (MultipartFile image:
                 images) {
             String fileName = auction.getId() + "_" + new Date().getTime() + ".jpg";
             String pathName = "./images/auction_images/" + auction.getId() + "/" +  fileName;
             Picture picture = new Picture(fileName,auction);
             pictureRepository.save(picture);
             File upl = new File(pathName);
             upl.createNewFile();
             FileOutputStream fout = new FileOutputStream(upl);
             fout.write(image.getBytes());
             fout.close();

        }
    }


    public Auction toAuction(AuctionDomain auctionDomain) {
        Category category = categoryRepository.findById(auctionDomain.getCategory().getId()).orElseThrow(() ->
                new NotFoundException(auctionDomain.getCategory().getId(), Category.class));
        Auction auction = new Auction(auctionDomain.getTitle(), auctionDomain.getDescription(),
                auctionDomain.getBase_price(), category, new Date(auctionDomain.getDate()),
                userDetailsService.getUser(), auctionDomain.getMax_number());
        return auction;
    }



    public AuctionDomain toAuctionDomain(Auction auction){
        AuctionDomain auctionDomain = new AuctionDomain(auction.getTitle(),auction.getDescription(),
                auction.getBase_price(),auction.getDate().getTime(),auction.getCategory(),auction.getMax_number());
        auctionDomain.setState(auction.getState());
        auctionDomain.setId(auction.getId());
        List<Link> auctionPictures = Lists.newArrayList(pictureRepository.findAll()).stream().filter(picture ->
                picture.getFileName().startsWith(auction.getId() + "_")).map(
                picture -> linkTo(methodOn(AuctionController.class)
                        .getImage(auction.getId(),picture.getFileName())).withRel("image")
        ).collect(Collectors.toList());
        auctionDomain.setPictures(auctionPictures);

        return auctionDomain;
    }


    public List<Category> getCategory(){

        return Lists.newArrayList(categoryRepository.findAll());

    }


    public List<AuctionDomain> filter(int category_id) {
        List<AuctionDomain> auctions = this.getAll();
        return auctions.stream().filter(a -> a.getCategory().getId() == category_id).collect(Collectors.toList());
    }

    public List<AuctionDomain> getAll(){
        return Lists.newArrayList(auctionRepository.findAll()).stream()
                .map(auction -> toAuctionDomain(auction))
                .collect(Collectors.toList());
    }

    public AuctionDomain findById(int id) {
        Auction auction = auctionRepository.findById(id).orElseThrow( () -> new NotFoundException(id,Auction.class));
        return toAuctionDomain(auction);
    }


    public List<AuctionDomain> findByTitle(String title) {
        List<AuctionDomain> auctions = getAll();
        auctions = auctions.stream()
                .filter(a -> a.getTitle().startsWith(title))
                .collect(Collectors.toList());
        return auctions;
    }



    public Page<AuctionDomain> getPage(int page, int size) {
        Pageable firstPageWithTwoElements = PageRequest.of(page, size);
        ArrayList<AuctionDomain> auctions = new ArrayList<>();
        return auctionRepository.findAll(firstPageWithTwoElements).map(a -> toAuctionDomain(a));
    }




    //TODO : change exception handling!
    public Resource imageUpload(int id, String fileName) {
        String path = "./images/auction_images/" + id + "/" + fileName;
        Path filePath = Paths.get(path).toAbsolutePath().normalize();
        try {
            Resource resource = new UrlResource(filePath.toUri());
            return resource;
        } catch (IOException e) {

            //its not good!!
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Page<AuctionDomain> getHottest(PageRequest request) {
        return toAuctionDomainPage(auctionRepository.findHottest(request));
    }

    public Page<AuctionDomain> findByTitle(String title, PageRequest pageRequest) {
        return toAuctionDomainPage(auctionRepository.findByTitle(title, pageRequest));
    }

    private Page<AuctionDomain> toAuctionDomainPage(Page<Auction> auctionPage) {
        List<AuctionDomain> auctionDomainList = new ArrayList<>();
        auctionPage.forEach(auction -> auctionDomainList.add(auction.toAuctionDomain()));
        return new PageImpl<>(auctionDomainList);
    }

    public Page<AuctionDomain> findByCategory(String categoryName, PageRequest pageRequest) {
        if (categoryRepository.findByCategoryName(categoryName).isPresent())
            return toAuctionDomainPage(auctionRepository.findByCategory(categoryRepository.findByCategoryName(categoryName).get(), pageRequest));
        throw new NotFoundException(categoryName,Category.class);
    }
}
