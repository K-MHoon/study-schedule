package com.example.studyschedule.service.schedule;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.model.dto.schedule.ScheduleDto;
import com.example.studyschedule.repository.member.MemberRepository;
import com.example.studyschedule.repository.schedule.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<ScheduleDto> getMemberScheduleList(Long memberId) {
        validateExistedMemberId(memberId);
        return scheduleRepository.findAllByMember_Id(memberId).stream()
                .map(ScheduleDto::entityToDto)
                .collect(Collectors.toList());
    }

    private Member validateExistedMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("해당하는 멤버 id를 찾을 수 없습니다. id = %d", memberId)));
    }

}
