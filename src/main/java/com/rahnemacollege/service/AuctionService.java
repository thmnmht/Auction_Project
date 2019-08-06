package com.rahnemacollege.service;


import com.google.common.collect.Lists;
import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Category;
import com.rahnemacollege.model.Picture;
import com.rahnemacollege.repository.AuctionRepository;
import com.rahnemacollege.repository.CategoryRepository;
import com.rahnemacollege.util.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, CategoryRepository categoryRepository) {
        this.auctionRepository = auctionRepository;
        this.categoryRepository = categoryRepository;
    }

    public Auction addAuction(AuctionDomain auctionDomain) throws IOException {
        Auction auction = toAuction(auctionDomain);
        auctionRepository.save(auction);
        return auction;
    }


     public List<Picture> savePictures(String title,MultipartFile[] images) throws IOException {
         ArrayList<Picture> pictures = new ArrayList<>();
         for (MultipartFile image:
                 images) {

             String imageName = "image/" + new Date().getTime() + "_" + title + ".jpg";
             Picture picture = new Picture(imageName);
             pictures.add(picture);

             //saving image
             File upl = new File(imageName);
             upl.createNewFile();
             FileOutputStream fout = new FileOutputStream(upl);
             fout.write(image.getBytes());
             fout.close();

         }
         return pictures;
     }
    
    
    public Auction toAuction(AuctionDomain auctionDomain) throws IOException {
        Category category = categoryRepository.findById(auctionDomain.getCategory_id()).orElseThrow( () ->
            new NotFoundException(auctionDomain.getCategory_id(),Category.class));
        List<Picture> pictures = null;
        if(auctionDomain.getPictures() != null)
            pictures = savePictures(auctionDomain.getTitle(),auctionDomain.getPictures());
        return new Auction(auctionDomain.getTitle(),auctionDomain.getDescription(),auctionDomain.getBase_price(), pictures,category,auctionDomain.getDate());
    }

    public List<Category> getCategory(){

        return Lists.newArrayList(categoryRepository.findAll());

    }

    public Optional<Auction> findById(int id) {
        return auctionRepository.findById(id);
    }

    public List<Auction> getAll() {
        ArrayList<Auction> auctions = new ArrayList<>();
        auctionRepository.findAll().forEach(auctions::add);
        return auctions;
    }
}
