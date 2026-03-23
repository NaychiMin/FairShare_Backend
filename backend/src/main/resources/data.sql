INSERT INTO `fairshare`.`tb_badge`
(`badge_id`,
`badge_rule_type`,
`badge_scope`,
`badge_type`,
`description`,
`name`,
`rule_config`)
VALUES
(
    UUID_TO_BIN(UUID()),
    0,  -- SETTLEMENT
    1,  -- GROUP (1 based on enum order if PERSONAL=0, GROUP=1)
    0,  -- SETTLEMENT_COUNT
    'Yay! got 3',
    '3 SETTLEMENTS',
    '{"count": 3}'
),
(
    UUID_TO_BIN(UUID()),
    1,  -- EXPENSE
    1,  -- GROUP (1 based on enum order if PERSONAL=0, GROUP=1)
    1,  -- EXPENSE_COUNT
    'Yay! got 3',
    '3 EXPENSES',
    '{"count": 3}'
);