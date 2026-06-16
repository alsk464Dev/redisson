package com.example.redisson.controller;

import com.example.redisson.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/{voteId}")
    public ResponseEntity<Void> vote(@PathVariable Long voteId) {
        voteService.vote(voteId);
        return ResponseEntity.ok().build();
    }
}
