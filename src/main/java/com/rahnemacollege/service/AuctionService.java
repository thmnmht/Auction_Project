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

    public Auction addAuction(AuctionDomain auctionDomain,MultipartFile[] images) throws IOException {
        Auction auction = toAuction(auctionDomain);
        auctionRepository.save(auction);
        if(images != null)
            savePictures(auction.getId(),images);
        return auction;
    }


     public void savePictures(int id,MultipartFile[] images) throws IOException {
         ArrayList<Picture> pictures = new ArrayList<>();
         new File("./src/main/resources/image/" + id + "/" ).mkdir();
         for (MultipartFile image:
                 images) {
             String pathName = "./src/main/resources/image/" + id + "/" + new Date().getTime() + ".jpg";
             Picture picture = new Picture(pathName);
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
        Auction auction = new Auction(auctionDomain.getTitle(),auctionDomain.getDescription(),auctionDomain.getBase_price(),category,auctionDomain.getDate(),auctionDomain.getMax_number());
        return auction;
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
