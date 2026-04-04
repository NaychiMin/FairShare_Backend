CREATE SCHEMA IF NOT EXISTS fairshare;

-- STATIC ROLES
INSERT INTO `fairshare`.`static_role`
(`static_role_id`,
`name`)
VALUES
(
    UUID_TO_BIN('11111111-1111-1111-1111-111111111111'),
    'ADMIN'
),
(
    UUID_TO_BIN('22222222-2222-2222-2222-222222222222'),
    'USER'
)
ON DUPLICATE KEY UPDATE
name = VALUES(name);

-- GROUPS
INSERT INTO `fairshare`.`tb_role`
(`role_id`,
`name`,
`scope`)
VALUES
(
    1,
    'GROUP_ADMIN',
    'GROUP'
),
(
    2,
    'GROUP_MEMBER',
    'GROUP'
)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
scope = VALUES(scope);

-- BADGES
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
    UUID_TO_BIN('33333333-3333-3333-3333-333333333333'),
    2,  -- SETTLEMENT_TIMING
    1,  -- GROUP (1 based on enum order if PERSONAL=0, GROUP=1)
    0,  -- SETTLEMENT
    'You have made a settlement 30 minutes after the last balance!',
    'Early Bird',
    '{"timingInMin": 30}'
),
(
    UUID_TO_BIN('44444444-4444-4444-4444-444444444444'),
    1,  -- EXPENSE_COUNT
    1,  -- GROUP (1 based on enum order if PERSONAL=0, GROUP=1)
    1,  -- EXPENSE
    'You have added 5 expenses today!',
    'Record Keeper',
    '{"count": 5}'
)
ON DUPLICATE KEY UPDATE
description = VALUES(description),
name = VALUES(name),
rule_config = VALUES(rule_config);