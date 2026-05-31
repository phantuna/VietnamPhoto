-- V6__create_chat_tables.sql
-- Real-time chat: conversations (1-1) + chat_messages

CREATE TABLE conversations
(
    id            VARCHAR(36)  NOT NULL PRIMARY KEY,
    created_by    VARCHAR(255) NULL,
    created_date  DATE         NULL,
    deleted       INT          NOT NULL DEFAULT 0,
    deleted_at    DATETIME(6)  NULL,
    modified_by   VARCHAR(255) NULL,
    modified_date DATE         NULL,
    -- user1_id always = LEAST(user_a, user_b) to enforce uniqueness regardless of order
    user1_id      VARCHAR(36)  NOT NULL,
    user2_id      VARCHAR(36)  NOT NULL,
    CONSTRAINT uq_conversation UNIQUE (user1_id, user2_id),
    CONSTRAINT fk_conv_user1 FOREIGN KEY (user1_id) REFERENCES users (id),
    CONSTRAINT fk_conv_user2 FOREIGN KEY (user2_id) REFERENCES users (id)
);

CREATE TABLE chat_messages
(
    id              VARCHAR(36)  NOT NULL PRIMARY KEY,
    created_by      VARCHAR(255) NULL,
    created_date    DATE         NULL,
    deleted         INT          NOT NULL DEFAULT 0,
    deleted_at      DATETIME(6)  NULL,
    modified_by     VARCHAR(255) NULL,
    modified_date   DATE         NULL,
    conversation_id VARCHAR(36)  NOT NULL,
    sender_id       VARCHAR(36)  NOT NULL,
    content         TEXT         NOT NULL,
    is_read         TINYINT(1)   NOT NULL DEFAULT 0,
    sent_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_msg_conversation FOREIGN KEY (conversation_id) REFERENCES conversations (id),
    CONSTRAINT fk_msg_sender FOREIGN KEY (sender_id) REFERENCES users (id)
);

-- Index để query nhanh tin nhắn theo conversation
CREATE INDEX idx_chat_messages_conversation ON chat_messages (conversation_id, sent_at DESC);
-- Index để query nhanh conversations của 1 user
CREATE INDEX idx_conversations_user1 ON conversations (user1_id);
CREATE INDEX idx_conversations_user2 ON conversations (user2_id);
