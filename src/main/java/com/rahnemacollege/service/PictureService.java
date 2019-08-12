package com.rahnemacollege.service;

import com.google.common.collect.Lists;
import com.rahnemacollege.model.Auction;
import com.rahnemacollege.model.Picture;
import com.rahnemacollege.repository.PictureRepository;
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
    private PictureRepository repository;

    public void save(MultipartFile pic, String path, Auction auction) throws IOException {
        Picture picture = new Picture(path,auction);
        repository.save(picture);
        save(pic,path);
    }

    public List<Picture> getAll(){
        return Lists.newArrayList(repository.findAll());
    }

    public void save(MultipartFile pic, String path) throws IOException {
        File upl = new File(path);
        upl.createNewFile();
        FileOutputStream fout = new FileOutputStream(upl);
        fout.write(pic.getBytes());
        fout.close();
    }
}
