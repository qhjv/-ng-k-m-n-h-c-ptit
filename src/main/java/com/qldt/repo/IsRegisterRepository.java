package com.qldt.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qldt.model.IsRegister;

@Repository
public interface IsRegisterRepository extends JpaRepository<IsRegister, Integer> {
}
