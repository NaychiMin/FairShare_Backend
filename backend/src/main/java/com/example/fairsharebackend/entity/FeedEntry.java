package com.example.fairsharebackend.entity;

import com.example.fairsharebackend.constant.FeedEntryType;
import com.example.fairsharebackend.constant.GroupField;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "tb_feed_entry",
        indexes = {
                @Index(name = "idx_feed_group_created", columnList = "group_group_id, created_date")
        }
)
public class FeedEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID feedEntryId;

    @Column(nullable = false)
    private FeedEntryType feedEntryType;

    // Expense Added
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private Expense expenseAdded;

    // Settlement Added
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private Settlement settlementAdded;

    // Badge Earned
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private UserBadge userBadgeEarned;

    // Group Updated or Fully Settled
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Group group;

    @Column
    private GroupField groupUpdatedField;
    @Column
    private String groupUpdatedFieldOld;
    @Column
    private String groupUpdatedFieldNew;
    @Column
    private LocalDateTime createdDate;

    public FeedEntry() {
        // Required by JPA (Hibernate) for entity instantiation via reflection
    }

    public UUID getFeedEntryId() {
        return feedEntryId;
    }

    public void setFeedEntryId(UUID feedEntryId) {
        this.feedEntryId = feedEntryId;
    }

    public FeedEntryType getFeedEntryType() {
        return feedEntryType;
    }

    public void setFeedEntryType(FeedEntryType feedEntryType) {
        this.feedEntryType = feedEntryType;
    }

    public Expense getExpenseAdded() {
        return expenseAdded;
    }

    public void setExpenseAdded(Expense expenseAdded) {
        this.expenseAdded = expenseAdded;
    }

    public Settlement getSettlementAdded() {
        return settlementAdded;
    }

    public void setSettlementAdded(Settlement settlementAdded) {
        this.settlementAdded = settlementAdded;
    }

    public UserBadge getUserBadgeEarned() {
        return userBadgeEarned;
    }

    public void setUserBadgeEarned(UserBadge userBadgeEarned) {
        this.userBadgeEarned = userBadgeEarned;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public GroupField getGroupUpdatedField() {
        return groupUpdatedField;
    }

    public void setGroupUpdatedField(GroupField groupUpdatedField) {
        this.groupUpdatedField = groupUpdatedField;
    }

    public String getGroupUpdatedFieldOld() {
        return groupUpdatedFieldOld;
    }

    public void setGroupUpdatedFieldOld(String groupUpdatedFieldOld) {
        this.groupUpdatedFieldOld = groupUpdatedFieldOld;
    }

    public String getGroupUpdatedFieldNew() {
        return groupUpdatedFieldNew;
    }

    public void setGroupUpdatedFieldNew(String groupUpdatedFieldNew) {
        this.groupUpdatedFieldNew = groupUpdatedFieldNew;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
