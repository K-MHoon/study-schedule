package com.example.service;

import com.example.service.entity.member.Member;
import com.example.service.helper.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@WithMockUser(username = "testMember", authorities = {"USER"})
public class TestHelper {

    @Autowired
    protected MemberHelper memberHelper;

    @Autowired
    protected ScheduleHelper scheduleHelper;

    @Autowired
    protected TodoHelper todoHelper;

    @Autowired
    protected StudyHelper studyHelper;

    @Autowired
    protected ScheduleTodoHelper scheduleTodoHelper;

    @Autowired
    protected StudyCodeHelper studyCodeHelper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected Member member;

    @BeforeEach
    void setup() {
        member = memberHelper.createSimpleMember();
    }

    protected void entityManagerFlushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
