package com.example.fairsharebackend.controller;

import com.example.fairsharebackend.entity.FeedEntry;
import com.example.fairsharebackend.entity.dto.request.FeedEntryFilterRequestDto;
import com.example.fairsharebackend.entity.dto.response.FeedEntryResponseDto;
import com.example.fairsharebackend.service.FeedEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feed")
public class FeedController {
    private final FeedEntryService feedEntryService;
    public FeedController(
            FeedEntryService feedEntryService
    ) {
        this.feedEntryService = feedEntryService;
    }

    @PostMapping
    public Page<FeedEntryResponseDto> findFeedEntryFilteredSorted(@RequestBody FeedEntryFilterRequestDto dto) {
        return feedEntryService.findFeedEntryFilteredSorted(dto);
    }
}
