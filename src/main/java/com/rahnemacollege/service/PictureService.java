package com.rahnemacollege.service;

import com.google.common.collect.Lists;
import com.rahnemacollege.domain.SimpleUserDomain;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Picture;
import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.PictureRepository;
import com.rahnemacollege.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;



@Service
public class PictureService {

    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(PictureService.class);
    private final String IMAGE_PATH = "./usr/local/share/images";



    public List<Picture> getAll() {
        return Lists.newArrayList(pictureRepository.findAll());
    }

    public void save(MultipartFile pic, String path) throws IOException {
        logger.info("start to try save picture with path: " + path);
        File upl = new File(path);
        upl.createNewFile();
        FileOutputStream fout = new FileOutputStream(upl);
        fout.write(pic.getBytes());
        fout.close();
        logger.info("picture saved");
    }


    public void setAuctionPictures(Auction auction,MultipartFile[] images) {
        String path = IMAGE_PATH + "/auction_images/" + auction.getId() + "/";
        new File(path).mkdirs();
        logger.info("directory created");
        for (MultipartFile image :
                images) {
            String fileName = "/auction_images/" + auction.getId() + "/" +  new Date().getTime() + ".jpg";
            String pathName = IMAGE_PATH + fileName;
            try {
                saveAuctionPicture(image, pathName, auction,fileName);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }


    public void saveAuctionPicture(MultipartFile pic, String path, Auction auction,String file_name) throws IOException {
        Picture picture = new Picture(file_name, auction);
        pictureRepository.save(picture);
        logger.info("added picture to repository");
        save(pic, path);
    }

    public SimpleUserDomain setProfilePicture(User user,MultipartFile picture){
        int userId = user.getId();
        String path = IMAGE_PATH + "/profile_images/" + userId + "/";
        new File(path).mkdirs();
        String fileName = "/profile_images/" + userId + "/" + new Date().getTime() + ".jpg";
        String pathName = IMAGE_PATH +  fileName;
        try {
            save(picture, pathName);
            user.setPicture(fileName);
            userRepository.save(user);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return new SimpleUserDomain(user.getName(),user.getEmail());

    }
}
