package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.FeedEntry;
import com.example.fairsharebackend.entity.dto.request.FeedEntryFilterRequestDto;
import com.example.fairsharebackend.entity.dto.response.FeedEntryResponseDto;
import com.example.fairsharebackend.mapper.FeedEntryMapper;
import com.example.fairsharebackend.repository.FeedEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class FeedEntryServiceImpl implements FeedEntryService {
    private static final Logger log = LoggerFactory.getLogger(FeedEntryServiceImpl.class);
    private final FeedEntryRepository feedEntryRepository;
    private final FeedEntryMapper feedEntryMapper;
    public FeedEntryServiceImpl(
            FeedEntryRepository feedEntryRepository,
            FeedEntryMapper feedEntryMapper
    ) {
        this.feedEntryRepository = feedEntryRepository;
        this.feedEntryMapper = feedEntryMapper;
    }

    @Override
    public Page<FeedEntryResponseDto> findFeedEntryFilteredSorted(FeedEntryFilterRequestDto dto) {
        Sort.Direction dir = Sort.Direction.fromString(dto.getDirection());
        Pageable pageable = PageRequest.of(
                dto.getPage(),
                dto.getSize(),
                Sort.by(dir, dto.getSortBy())
        );

        Page<FeedEntry> page =  feedEntryRepository.findFeedEntryFilteredSorted(
                dto.getUserId(),
                dto.getGroupId(),
                pageable
        );
        return page.map(feedEntryMapper::toDto);
    }
}
