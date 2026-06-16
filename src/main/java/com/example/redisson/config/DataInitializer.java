package com.example.redisson.config;

import com.example.redisson.domain.VoteItem;
import com.example.redisson.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final VoteRepository voteRepository;

    @Override
    public void run(String... args) throws Exception {
        // 서버가 시작될 때 투표 항목을 미리 저장 (ID: 1)
        voteRepository.save(new VoteItem("통신사 할인이 더욱 다양해졌으면 좋겠어요."));
        System.out.println("=== 테스트용 초기 데이터 셋팅 완료 (ID: 1) ===");
    }
}
