package com.org.devexp.linkvalidator.repository;

import com.org.devexp.linkvalidator.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
}
