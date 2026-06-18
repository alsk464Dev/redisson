package com.example.redisson.service;

import com.example.redisson.domain.VoteItem;
import com.example.redisson.repository.VoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteServiceSync {

    private final VoteRepository voteRepository;

    /**
     * synchronized 키워드 추가
     * Transactional 어노테이션과 함께 존재할 때 동시성 제어가 될까?
     *
     * @param voteId
     */
    @Transactional
    public synchronized void vote(Long voteId) {
        VoteItem voteItem = voteRepository.findById(voteId)
                                          .orElseThrow(() -> new IllegalArgumentException("해당 투표 항목이 존재하지 않습니다."));

        voteItem.increaseVoteCount();
    }
}
