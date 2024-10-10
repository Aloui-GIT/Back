package com.example.generateurformulaire.services;

import com.example.generateurformulaire.entities.Media;
import com.example.generateurformulaire.repository.MediaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class MediaService {
    @Autowired
    MediaRepository mediaRepo;



    public Optional<Media> getOne(Long id){
        return mediaRepo.findById(id);
    }

    public void save(Media media){
        mediaRepo.save(media);
    }

    public void delete(Long id){
        mediaRepo.deleteById(id);
    }

    public boolean exists(Long id){
        return mediaRepo.existsById(id);
    }
}
