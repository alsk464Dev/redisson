package com.example.redisson.service;

import com.example.redisson.domain.VoteItem;
import com.example.redisson.repository.VoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;

    @Transactional
    public void vote(Long voteId) {
        VoteItem voteItem = voteRepository.findById(voteId)
                                          .orElseThrow(() -> new IllegalArgumentException("해당 투표 항목이 존재하지 않습니다."));

        voteItem.increaseVoteCount();
    }
}
