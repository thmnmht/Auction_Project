package com.rahnemacollege.service;


import com.google.common.collect.Lists;
import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Category;
import com.rahnemacollege.model.Picture;
import com.rahnemacollege.repository.AuctionRepository;
import com.rahnemacollege.repository.CategoryRepository;
import com.rahnemacollege.repository.PictureRepository;
import com.rahnemacollege.util.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final PictureRepository pictureRepository;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, CategoryRepository categoryRepository, PictureRepository pictureRepository) {
        this.auctionRepository = auctionRepository;
        this.categoryRepository = categoryRepository;
        this.pictureRepository = pictureRepository;
    }

    public AuctionDomain addAuction(AuctionDomain auctionDomain,MultipartFile[] images) throws IOException {
        Auction auction = toAuction(auctionDomain);
        auctionRepository.save(auction);
        if(images != null)
            savePictures(auction,images);
        return toAuctionDomain(auction);
    }


     public void savePictures(Auction auction,MultipartFile[] images) throws IOException {
         ArrayList<Picture> pictures = new ArrayList<>();
         new File("./src/main/resources/image/" + auction.getId() + "/" ).mkdir();
         for (MultipartFile image:
                 images) {
             String pathName = "./src/main/resources/image/" + auction.getId() + "/" + new Date().getTime() + ".jpg";
             Picture picture = new Picture(pathName,auction);
             pictureRepository.save(picture);
             pictures.add(picture);

             //saving image
             File upl = new File(pathName);
             upl.createNewFile();
             FileOutputStream fout = new FileOutputStream(upl);
             fout.write(image.getBytes());
             fout.close();

         }
     }
    
    
    public Auction toAuction(AuctionDomain auctionDomain){
        Category category = categoryRepository.findById(auctionDomain.getCategory_id()).orElseThrow( () ->
            new NotFoundException(auctionDomain.getCategory_id(),Category.class));
        Auction auction = new Auction(auctionDomain.getTitle(),auctionDomain.getDescription(),auctionDomain.getBase_price(),category,new Date(auctionDomain.getDate()),auctionDomain.getMax_number());
        return auction;
    }


    public AuctionDomain toAuctionDomain(Auction auction){
        AuctionDomain auctionDomain = new AuctionDomain(auction.getTitle(),auction.getDescription(),auction.getBase_price(),auction.getDate().getTime(),auction.getCategory().getId(),auction.getMax_number());
        auctionDomain.setState(auction.getState());
        auctionDomain.setId(auction.getId());
        //TODO : add pictures to auctionDomain
        return auctionDomain;
    }

    public List<Category> getCategory(){

        return Lists.newArrayList(categoryRepository.findAll());

    }

    public List<AuctionDomain> filter(int category_id){
        List<AuctionDomain> auctions = getAll();
        return auctions.stream().filter(a -> a.getCategory_id() == category_id).collect(Collectors.toList());
    }

    public AuctionDomain findById(int id) {
        Auction auction = auctionRepository.findById(id).orElseThrow( () -> new NotFoundException(id,Auction.class));
        return toAuctionDomain(auction);
    }

    public List<AuctionDomain> findByTitle(String title) {
        List<AuctionDomain> auctions = getAll();
        auctions = auctions.stream().filter(a -> a.getTitle().startsWith(title)).collect(Collectors.toList());
        return auctions;
    }


    public List<AuctionDomain> getAll() {
        ArrayList<AuctionDomain> auctions = new ArrayList<>();
        auctionRepository.findAll().forEach(auction ->  {
            auctions.add(toAuctionDomain(auction));
        });
        return auctions;
    }
}
