package com.example.studyschedule.repository.schedule;

import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.ScheduleTodo;
import com.example.studyschedule.entity.schedule.Todo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleTodoRepository extends JpaRepository<ScheduleTodo, Long> {

    @EntityGraph(attributePaths = {"schedule", "todo"})
    List<ScheduleTodo> findAllByScheduleIn(List<Schedule> schedule);

    @Query("select st from ScheduleTodo st join fetch st.schedule sts join fetch st.todo stt where sts = :schedule and stt in :todoList")
    List<ScheduleTodo> findAllByScheduleAndTodoIn(@Param("schedule") Schedule schedule, @Param("todoList") List<Todo> todoList);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from ScheduleTodo st where st.schedule.id in (:scheduleIdList)")
    int deleteAllByScheduleIdList(@Param("scheduleIdList") List<Long> scheduleIdList);

    List<ScheduleTodo> findAllByTodo_IdIn(List<Long> todoIdList);
}
