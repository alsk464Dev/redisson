package com.example.redisson.service;

import com.example.redisson.domain.VoteItem;
import com.example.redisson.repository.VoteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class VoteServiceSyncTest {

    @Autowired
    private VoteServiceSync voteServiceSync;

    @Autowired
    private VoteRepository voteRepository;

    private Long voteId;

    @BeforeEach
    void setUp() {

        VoteItem voteItem = voteRepository.save(new VoteItem("통신사 할인이 더욱 다양해졌으면 좋겠어요."));
        voteId = voteItem.getId();
    }

    @Test
    @DisplayName("100명의 사용자가 동시에 투표시 투표 수는 100이 되어야 한다. (하지만 실패할 것)")
    void normalVoteTest() throws InterruptedException {

        // 100개의 스레드를 가진 스레드 풀 생성
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // 100개의 작업이 모두 끝날 때까지 대기하기 위한 Latch
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    voteServiceSync.vote(voteId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드의 작업이 끝날 때까지 메인 스레드 대기
        executorService.shutdown();

        VoteItem voteItem = voteRepository.findById(voteId).orElseThrow();

        System.out.println("=== 최종 투표 수: " + voteItem.getVoteCount() + " ===");

        Assertions.assertEquals(100, voteItem.getVoteCount());
    }

}