package com.example.redisson.repository;

import com.example.redisson.domain.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<VoteItem, Long> {
}
