package com.rahnemacollege.service;


import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Picture;
import com.rahnemacollege.repository.AuctionRepository;
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

    @Autowired
    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
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
        return new Auction(auctionDomain.getTitle(),auctionDomain.getDescription(),auctionDomain.getBase_price(), savePictures(auctionDomain.getTitle(),auctionDomain.getPictures()),auctionDomain.getCategory(),auctionDomain.getDate());
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
