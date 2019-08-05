package com.rahnemacollege.service;


import com.rahnemacollege.domain.AuctionDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Picture;
import com.rahnemacollege.repository.AuctionRepository;
import com.rahnemacollege.util.ResourceAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ResourceAssembler assembler;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, ResourceAssembler assembler) {
        this.auctionRepository = auctionRepository;
        this.assembler = assembler;
    }

    public Auction addAuction(AuctionDomain auctionDomain){
        Auction auction = toAuction(auctionDomain);
        auctionRepository.save(auction);
        return auction;
    }


     public List<Picture> makePictures(MultipartFile... images) throws IOException {
         ArrayList<Picture> pictures = new ArrayList<>();

         int counter = 1;
         for (MultipartFile image:
                 images) {

             String imageName = "image/" + new Date().getTime() + "@" + counter + ".jpg";
             counter++;
             Picture picture = new Picture(imageName);
             pictures.add(picture);

             //getting image
             File upl = new File(imageName);
             upl.createNewFile();
             FileOutputStream fout = new FileOutputStream(upl);
             fout.write(image.getBytes());
             fout.close();

         }
         return pictures;
     }
    
    
    public Auction toAuction(AuctionDomain auctionDomain){
        return new Auction(auctionDomain.getTitle(),auctionDomain.getDescription(),auctionDomain.getBase_price(),auctionDomain.getPictures(),auctionDomain.getCategory(),auctionDomain.getDate());
    }

    public Optional<Auction> findById(int id) {
        return auctionRepository.findById(id);
    }

    public Resource<Auction> toResource(Auction auction) {
        return assembler.toResource(auction);
    }

    public List<Auction> getAll() {
        ArrayList<Auction> auctions = new ArrayList<>();
        auctionRepository.findAll().forEach(auctions::add);
        return auctions;
    }
}
