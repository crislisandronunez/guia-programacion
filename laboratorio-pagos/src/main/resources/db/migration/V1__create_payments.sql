CREATE TABLE payments (
    pay_id UUID NOT NULL,
    order_reference VARCHAR(255) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    authorized_amount NUMERIC(19, 2) NOT NULL,
    authorized_currency VARCHAR(255) NOT NULL,
    captured_amount NUMERIC(19, 2) NOT NULL,
    captured_currency VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    update_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL,

    CONSTRAINT pk_payments PRIMARY KEY (pay_id),
    CONSTRAINT ck_payments_amount CHECK (amount >= 0),
    CONSTRAINT ck_payments_authorized_amount CHECK (authorized_amount >= 0),
    CONSTRAINT ck_payments_captured_amount CHECK (captured_amount >= 0),
    CONSTRAINT ck_payments_currency CHECK (currency IN ('ARS', 'USD')),
    CONSTRAINT ck_payments_authorized_currency
        CHECK (authorized_currency IN ('ARS', 'USD')),
    CONSTRAINT ck_payments_captured_currency
        CHECK (captured_currency IN ('ARS', 'USD')),
    CONSTRAINT ck_payments_state
        CHECK (state IN ('CREATED', 'AUTHORIZED', 'CAPTURED', 'REJECTED', 'VOIDED')),
    CONSTRAINT ck_payments_version CHECK (version >= 0)
);
