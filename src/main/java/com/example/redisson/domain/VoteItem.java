package com.example.redisson.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Long voteCount;

    public VoteItem(String title) {
        this.title = title;
        this.voteCount = 0L;
    }

    public void increaseVoteCount() {
        this.voteCount = this.voteCount + 1;
    }
}
