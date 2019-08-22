package com.rahnemacollege.repository;

import com.rahnemacollege.model.Picture;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PictureRepository extends CrudRepository<Picture, Integer> {

}
