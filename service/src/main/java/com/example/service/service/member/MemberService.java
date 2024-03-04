package com.example.service.service.member;

import com.example.common.entity.member.Member;
import com.example.common.entity.schedule.Schedule;
import com.example.common.model.dto.member.MemberDto;
import com.example.common.model.dto.security.TokenInfo;
import com.example.common.repository.member.MemberRepository;
import com.example.common.repository.schedule.ScheduleHistoryRepository;
import com.example.common.repository.schedule.ScheduleRepository;
import com.example.common.repository.schedule.ScheduleTodoRepository;
import com.example.common.repository.schedule.TodoRepository;
import com.example.common.repository.study.StudyMemberRepository;
import com.example.common.repository.study.StudyRepository;
import com.example.service.security.provider.JwtTokenProvider;
import com.example.service.service.request.MemberServiceRequest;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleTodoRepository scheduleTodoRepository;
    private final ScheduleHistoryRepository scheduleHistoryRepository;
    private final TodoRepository todoRepository;

    private final MemberCommonService commonService;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 스터디 전체 회원 정보를 가지고 온다.
     *
     * @return 스터디 회원 정보 목록
     */
    @Transactional(readOnly = true)
    public List<MemberDto> getMemberList() {
        return memberRepository.findAll().stream()
                .map(MemberDto::entityToDto)
                .collect(Collectors.toList());
    }


    /**
     * 스터디 단일 회원 정보를 가지고 온다.
     *
     * @param id 스터디 회원 ID
     * @return 단일 스터디 회원 정보
     */
    @Transactional(readOnly = true)
    public MemberDto getMemberById(Long id) {
        return MemberDto.entityToDto(commonService.validateExistedMemberById(id));
    }


    /**
     * 새로운 스터디 회원을 추가한다.
     *
     * @param request 신규 스터디 회원 정보를 가진 객체
     */
    @Transactional
    public void createMember(MemberServiceRequest.CreateMember request) {
        memberRepository.findByMemberId(request.getMemberId())
                .ifPresent(member -> {
                    log.error("동일한 멤버가 존재합니다. ID = {}", member.getMemberId());
                    throw new IllegalArgumentException("동일한 멤버가 존재합니다. ID = " + member.getMemberId());
                });

        Member newMember = Member.builder()
                .memberId(request.getMemberId())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of("USER"))
                .name(request.getName())
                .age(request.getAge())
                .build();

        memberRepository.save(newMember);
    }

    @Transactional(readOnly = true)
    public MemberDto getMemberProfile() {
        Member loggedInMember = commonService.getLoggedInMember();
        return MemberDto.entityToDto(loggedInMember);
    }

    @Transactional
    public void updateMemberProfile(MemberServiceRequest.UpdateMemberProfile request) {
        if(request.getAge() < 1 || request.getAge() > 100) {
            throw new IllegalArgumentException("나이는 1 ~ 100살 까지만 입력 할 수 있습니다.");
        }

        Member loggedInMember = commonService.getLoggedInMember();
        if(!StringUtils.isEmpty(request.getPassword())) {
            loggedInMember.updatePassword(passwordEncoder.encode(request.getPassword()));
        }
        loggedInMember.updateName(request.getName());
        loggedInMember.updateAge(request.getAge());
    }

    @Transactional
    public void deleteMember() {
        Member loggedInMember = commonService.getLoggedInMember();

        if(studyRepository.existsByLeader(loggedInMember)) {
            throw new IllegalArgumentException("운영중인 스터디가 존재하여 탈퇴할 수 없습니다.");
        }
        if(studyMemberRepository.existsByMember(loggedInMember)) {
            throw new IllegalArgumentException("가입된 스터디가 존재하여 탈퇴할 수 없습니다. 모든 스터디를 탈퇴해주세요.");
        }
        List<Schedule> scheduleList = scheduleRepository.findAllByMember_Id(loggedInMember.getId());
        List<Long> scheduleIdList = scheduleList.stream().map(Schedule::getId).collect(Collectors.toList());
        scheduleTodoRepository.deleteAllByScheduleIdList(scheduleIdList);
        scheduleHistoryRepository.deleteAllByScheduleIdList(scheduleIdList);
        scheduleRepository.deleteAllByScheduleIdList(scheduleIdList);
        todoRepository.deleteAllByMember(loggedInMember);
        memberRepository.delete(loggedInMember);
    }

    @Getter
    public static class ClientAuthInfo {

        private boolean auth;
        private boolean refresh;
        private TokenInfo tokenInfo;

        public ClientAuthInfo(boolean auth) {
            this.auth = auth;
        }

        public ClientAuthInfo(boolean auth, boolean refresh, TokenInfo tokenInfo) {
            this.auth = auth;
            this.refresh = refresh;
            this.tokenInfo = tokenInfo;
        }
    }
}
