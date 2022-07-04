CREATE EXTENSION IF NOT EXISTS timescaledb;

CREATE TABLE IF NOT EXISTS raw_blocks (
  height NUMERIC(30,0) PRIMARY KEY,
  created_at TIMESTAMP WITH TIME ZONE,
  data JSONB
);

CREATE TABLE IF NOT EXISTS raw_transactions (
  hash text PRIMARY KEY,
  block_height NUMERIC(30,0) REFERENCES raw_blocks(height),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  mined_at TIMESTAMP WITH TIME ZONE,
  dropped_at TIMESTAMP WITH TIME ZONE,
  data JSONB
);

CREATE TABLE IF NOT EXISTS processed_transactions (
  hash TEXT,
  type TEXT,
  block_height NUMERIC(30,0) REFERENCES raw_blocks(height),
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  mined_at TIMESTAMP WITH TIME ZONE,
  dropped_at TIMESTAMP WITH TIME ZONE,
  block_hash TEXT,
  sender TEXT,
  gas NUMERIC(30,0),
  gas_price NUMERIC(30,0),
  max_fee_per_gas NUMERIC(30,0),
  max_priority_fee_per_gas NUMERIC(30,0),
  input TEXT,
  nonce NUMERIC(30,0),
  receiver TEXT,
  transaction_index NUMERIC(30,0),
  value NUMERIC(30,0),
  status text,
  PRIMARY KEY (hash, created_at)
);

SELECT create_hypertable('processed_transactions', 'created_at');
