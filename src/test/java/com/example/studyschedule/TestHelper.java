package com.example.studyschedule;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.repository.member.MemberRepository;
import com.example.studyschedule.repository.schedule.ScheduleRepository;
import com.example.studyschedule.service.member.MemberCommonService;
import com.example.studyschedule.service.member.MemberService;
import com.example.studyschedule.service.schedule.TodoCommonService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@Transactional
public class TestHelper {

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected TodoCommonService todoCommonService;

    @Autowired
    protected MemberCommonService memberCommonService;

    @Autowired
    protected ScheduleRepository scheduleRepository;

    protected List<Member> createTestMembersAndSaveByCount(int count) {
        return IntStream.range(0, count)
                .mapToObj(c -> {
                    Member member = Member.builder().memberId("testMember" + c).password("testPassword").build();
                    return memberRepository.save(member);
                })
                .collect(Collectors.toList());
    }

    protected List<Schedule> createTestSchedulesAndSaveByCount(Member member, int count){
        return IntStream.range(0, count)
                .mapToObj(c -> {
                    Schedule schedule = new Schedule(member, LocalDateTime.now(), LocalDateTime.now().plusDays(10), IsUse.Y, "testSchedule" + c);
                    return scheduleRepository.save(schedule);
                })
                .collect(Collectors.toList());
    }

    protected Member createSimpleMember() {
        Member member = Member.builder().memberId("testMember").password("testPassword").build();
        return memberRepository.save(member);
    }

}
