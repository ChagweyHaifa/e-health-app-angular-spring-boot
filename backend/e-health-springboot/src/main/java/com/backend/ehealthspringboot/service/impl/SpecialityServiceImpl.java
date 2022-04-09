package com.backend.ehealthspringboot.service.impl;

import com.backend.ehealthspringboot.domain.Review;
import com.backend.ehealthspringboot.domain.Speciality;
import com.backend.ehealthspringboot.repository.ReviewRepository;
import com.backend.ehealthspringboot.repository.SpecialityRepository;
import com.backend.ehealthspringboot.service.SpecialityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialityServiceImpl implements SpecialityService {
    private SpecialityRepository specialityRepository;

    @Autowired
    public SpecialityServiceImpl(SpecialityRepository specialityRepository){
        this.specialityRepository = specialityRepository;
    }

    @Override
    public List<Speciality> getSpecialities() {
        return specialityRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }
}
