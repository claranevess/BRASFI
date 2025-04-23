package com.brasfi.platforma.repository;

import com.brasfi.platforma.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
