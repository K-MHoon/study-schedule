package com.example.studyschedule.service.study;

import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.model.dto.study.StudyDto;
import com.example.studyschedule.repository.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;

    @Transactional(readOnly = true)
    public List<StudyDto> getPublicStudyList() {
        return studyRepository.findAllBySecretAndIsUse(false, IsUse.Y)
                .stream().map(StudyDto::entityToDto)
                .collect(Collectors.toList());
    }
}

