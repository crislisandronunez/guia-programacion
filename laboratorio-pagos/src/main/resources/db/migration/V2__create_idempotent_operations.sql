CREATE TABLE idempotent_operations (
    operation_id UUID NOT NULL,
    scope VARCHAR(255) NOT NULL,
    idempotency_key VARCHAR(255) NOT NULL,
    request_hash VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    http_status INTEGER,
    response_body TEXT,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    update_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL,

    CONSTRAINT pk_idempotent_operations PRIMARY KEY (operation_id),
    CONSTRAINT uk_idempotent_operations_scope_key UNIQUE (scope, idempotency_key),
    CONSTRAINT ck_idempotent_operations_state
        CHECK (state IN ('IN_PROGRESS', 'COMPLETED', 'FAILED')),
    CONSTRAINT ck_idempotent_operations_version CHECK (version >= 0)
);
