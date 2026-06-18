# 📝 [프로젝트 기록] 투표 시스템 동시성 제어 및 분산 락 구현

## 📌 1단계: 동시성 장애 재현 및 문제 정의

### 1. 배경 및 목적
* **배경**: 기존 SI 환경에서 주도적으로 기술을 선택하기보다 비즈니스 요구사항에 맞춰 수동적으로 개발했던 아쉬움이 있었음.
* **목적**: 대규모 트래픽 상황에서 발생할 수 있는 동시성 장애 환경을 직접 재현하고, 단계별로 해결하는 과정을 기록 및 검증함.

---

### 2. 문제 상황 정의 (Race Condition)
* **시나리오**: 특정 투표 항목(ID: 1)에 대해 100명의 사용자가 동시에 투표를 진행함.
* **기대 결과**: 최종 투표 수(`voteCount`) = `100`
* **실제 결과**: 최종 투표 수(`voteCount`) = `20 ~ 90` 내외의 값 (데이터 정합성 실패)



#### 🔴 원인 분석 (갱신 손실 - Lost Update)
1. Spring Boot의 Tomcat은 멀티 스레드로 요청을 처리함.
2. 스레드 A와 스레드 B가 거의 동시에 DB에서 `voteCount = 0`인 데이터를 조회함.
3. 스레드 A가 `0 + 1 = 1`을 계산하고 DB에 반영(`UPDATE`)함.
4. 스레드 B 역시 본인이 읽었던 `voteCount = 0`을 기준으로 `0 + 1 = 1`을 계산하여 DB에 덮어씀.
5. **결과적으로 투표는 2번 처리되었으나, DB에는 1만 반영되는 데이터 유실 발생.**

---

### 3. 장애 재현 환경 구축

#### ① 도메인 및 비즈니스 로직 (Spring Boot, JPA, H2)
* Entity 내에 값을 1씩 증가시키는 비즈니스 메서드 구현
```java
// VoteItem Entity
public void increaseVoteCount() {
    this.voteCount = this.voteCount + 1;
}

// VoteService
@Transactional
public void vote(Long voteId) {
    VoteItem voteItem = voteRepository.findById(voteId)
                                      .orElseThrow(() -> new IllegalArgumentException("해당 투표 항목이 존재하지 않습니다."));

    voteItem.increaseVoteCount();
}
```

#### ② 검증1: VoteService 테스트 코드 구현 (normal)
* VoteServiceTest
* 자바 멀티 스레드 테스트 코드 구현
* ExecutorService, CountDownLatch를 활용하여 Race Condition 검증
* 결과: Assertions.assertEquals(100, voteItem.getVoteCount()); 실패.
* `Expected: 100 / Actual: 11`

#### ③ 검증2: JMeter 부하 테스트 (normal)
* Thread Group 설정: (Users: 100, Ramp-up: 0, Loop: 1) → HTTP Request (POST)
* 결과: 100개의 요청 모두 성공, DB 조회 시 `voteCount = 89`