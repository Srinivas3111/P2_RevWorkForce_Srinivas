package com.rev.app.repository;

import com.rev.app.entity.Employee;
import com.rev.app.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findByEmployee(Employee employee);

    List<Event> findByEventDateBetween(LocalDate start, LocalDate end);

    List<Event> findAllByOrderByEventDateDesc();
}
