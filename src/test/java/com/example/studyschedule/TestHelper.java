package com.example.studyschedule;

import com.example.studyschedule.entity.member.Member;
import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.ScheduleTodo;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.repository.schedule.ScheduleRepository;
import com.example.studyschedule.repository.schedule.ScheduleTodoRepository;
import com.example.studyschedule.repository.schedule.TodoRepository;
import com.example.studyschedule.repository.study.StudyMemberRepository;
import com.example.studyschedule.repository.study.StudyRegisterRepository;
import com.example.studyschedule.repository.study.StudyRepository;
import com.example.studyschedule.service.member.MemberCommonService;
import com.example.studyschedule.service.schedule.ScheduleCommonService;
import com.example.studyschedule.service.schedule.TodoCommonService;
import com.example.studyschedule.support.MemberHelper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@Transactional
@WithMockUser(username = "testMember", authorities = {"USER"})
public class TestHelper {

    @Autowired
    protected MemberHelper memberHelper;

    @Autowired
    protected StudyRepository studyRepository;

    @Autowired
    protected StudyMemberRepository studyMemberRepository;

    @Autowired
    protected TodoRepository todoRepository;

    @Autowired
    protected ScheduleRepository scheduleRepository;

    @Autowired
    protected ScheduleTodoRepository scheduleTodoRepository;

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected TodoCommonService todoCommonService;

    @Autowired
    protected MemberCommonService memberCommonService;

    @Autowired
    protected ScheduleCommonService scheduleCommonService;

    @Autowired
    protected StudyRegisterRepository studyRegisterRepository;

    protected Member member;

    @BeforeEach
    void setup() {
        member = memberHelper.createSimpleMember();
    }

    protected List<Schedule> createTestSchedulesAndSaveByCount(Member member, int count) {
        return IntStream.range(0, count)
                .mapToObj(c -> {
                    Schedule schedule = new Schedule(member, LocalDateTime.now(), LocalDateTime.now().plusDays(10), IsUse.Y, "testSchedule" + c);
                    return scheduleRepository.save(schedule);
                })
                .collect(Collectors.toList());
    }

    protected List<Schedule> createTestSchedulesAndSaveByCount(Member member, int count, Study study) {
        return IntStream.range(0, count)
                .mapToObj(c -> {
                    Schedule schedule = new Schedule(member, LocalDateTime.now(), LocalDateTime.now().plusDays(10), IsUse.Y, "testSchedule" + c, study);
                    return scheduleRepository.save(schedule);
                })
                .collect(Collectors.toList());
    }

    protected List<Todo> createTestTodosAndSaveByCount(Member member, int count) {
        return IntStream.range(0, count)
                .mapToObj(c -> {
                    Todo todo = new Todo("test Title " + c, "test Content " + c, member);
                    return todoRepository.save(todo);
                })
                .collect(Collectors.toList());
    }

    protected Study getStudyFixture(Member member) {
        return Study.ofPublic(member, "스터디 테스트", "스터디 설명", 10L, IsUse.Y);
    }

    protected List<ScheduleTodo> connectScheduleTodoList(Schedule schedule, List<Todo> todoList) {
        return todoList.stream().map(todo -> {
                    ScheduleTodo scheduleTodo = new ScheduleTodo(schedule, todo);
                    return scheduleTodoRepository.save(scheduleTodo);
                })
                .collect(Collectors.toList());
    }

    protected void entityManagerFlushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
