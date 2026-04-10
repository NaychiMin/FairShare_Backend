package com.example.fairsharebackend.service;

import com.example.fairsharebackend.constant.FeedEntryType;
import com.example.fairsharebackend.constant.GroupField;
import com.example.fairsharebackend.entity.*;
import com.example.fairsharebackend.repository.FeedEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FeedEntryEngineImpl implements FeedEntryEngine {
    private static final Logger log = LoggerFactory.getLogger(FeedEntryEngineImpl.class);
    private final FeedEntryRepository feedEntryRepository;
    public FeedEntryEngineImpl(
            FeedEntryRepository feedEntryRepository
    ) {
        this.feedEntryRepository = feedEntryRepository;
    }

    @Override
    @EventListener
    public void handleExpenseCreated(Expense event) {
        log.warn("handleExpenseCreated");
        logExpense(event);
    }

    @EventListener
    @Override
    public void handleGroupBalance(Group group) {
        log.warn("handleGroupBalance");
        logGroup(group);
    }

    public void logGroup(Group group) {
        log.info("logGroup :: {}", group.getGroupName());
        try {
            FeedEntry feedEntry = new FeedEntry();
            feedEntry.setGroupSettled(group);
            feedEntry.setGroup(group);
            feedEntry.setFeedEntryType(FeedEntryType.GROUP_ALL_SETTLED);
            feedEntry.setCreatedDate(LocalDateTime.now());
            feedEntryRepository.save(feedEntry);
            feedEntryRepository.flush();
        } catch (Exception e) {
            log.error("Exception :: {}", e.getMessage());
        }
    }

    @Override
    @EventListener
    public void handleSettlementCreated(Settlement event) {
        log.warn("handleSettlementCreated");
        logGroupFullySettled(event.getGroup());
    }

    @Override
    @EventListener
    public void handleBadgeCreated(UserBadge event) {
        log.warn("handleBadgeCreated");
        logUserBadge(event);
    }

    @Override
    @EventListener
    public void handleGroupUpdated(GroupUpdatedEvent event) {
        if (event.getOldValue() == null || event.getNewValue() == null) {
            log.warn("handleGroupUpdated: oldValue {} , newValue {}", event.getOldValue(), event.getNewValue());
            return;
        }

        if (event.getOldValue().strip().equals(event.getNewValue())) {
            log.warn("handleGroupUpdated: oldValue {} equals newValue {}", event.getOldValue(), event.getNewValue());
            return;
        }
        logGroupUpdated(event.getGroup(), event.getGroupField(), event.getOldValue(), event.getNewValue());
    }

    public void logExpense(Expense expense) {
        log.info("logExpense :: {}", expense.getDescription());
        try {
            FeedEntry feedEntry = new FeedEntry();
            feedEntry.setExpenseAdded(expense);
            feedEntry.setGroup(expense.getGroup());
            feedEntry.setFeedEntryType(FeedEntryType.EXPENSE_ADDED);
            feedEntry.setCreatedDate(LocalDateTime.now());
            feedEntryRepository.save(feedEntry);
            feedEntryRepository.flush();
        } catch (Exception e) {
            log.error("Exception :: {}", e.getMessage());
        }
    }

    public void logUserBadge(UserBadge userBadge) {
        log.info("logBadge :: {}", userBadge.getUserBadgeId());
        try {
            FeedEntry feedEntry = new FeedEntry();
            feedEntry.setUserBadgeEarned(userBadge);
            feedEntry.setGroup(userBadge.getGroup());
            feedEntry.setFeedEntryType(FeedEntryType.BADGE_EARNED);
            feedEntry.setCreatedDate(LocalDateTime.now());
            feedEntryRepository.save(feedEntry);
            feedEntryRepository.flush();
        } catch (Exception e) {
            log.error("Exception :: {}", e.getMessage());
        }
    }

    public void logGroupFullySettled(Group group) {
        log.info("logGroupFullySettled :: {}", group.getGroupName());
        try {
            FeedEntry feedEntry = new FeedEntry();
            feedEntry.setGroup(group);
            feedEntry.setFeedEntryType(FeedEntryType.GROUP_ALL_SETTLED);
            feedEntry.setCreatedDate(LocalDateTime.now());
            feedEntryRepository.save(feedEntry);
            feedEntryRepository.flush();
        } catch (Exception e) {
            log.error("Exception :: {}", e.getMessage());
        }
    }

    public void logGroupUpdated(Group group, GroupField groupUpdatedField, String oldValue, String newValue) {
        log.info("logGroupUpdated :: {}", group.getGroupName());
        try {
            FeedEntry feedEntry = new FeedEntry();
            feedEntry.setGroup(group);
            feedEntry.setGroupUpdatedField(groupUpdatedField);
            feedEntry.setGroupUpdatedFieldOld(oldValue);
            feedEntry.setGroupUpdatedFieldNew(newValue);
            feedEntry.setFeedEntryType(FeedEntryType.GROUP_UPDATE);
            feedEntry.setCreatedDate(LocalDateTime.now());
            feedEntryRepository.save(feedEntry);
            feedEntryRepository.flush();
        } catch (Exception e) {
            log.error("Exception :: {}", e.getMessage());
        }
    }
}
