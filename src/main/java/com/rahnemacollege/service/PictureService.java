package com.rahnemacollege.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@Service
public class PictureService {

    public void save(MultipartFile pic,String path) throws IOException {
        File upl = new File(path);
        upl.createNewFile();
        FileOutputStream fout = new FileOutputStream(upl);
        fout.write(pic.getBytes());
        fout.close();
    }



}
