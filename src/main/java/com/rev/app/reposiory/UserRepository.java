package com.rev.app.reposiory;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rev.app.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{

}