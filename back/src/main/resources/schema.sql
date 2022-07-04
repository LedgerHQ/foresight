CREATE TABLE IF NOT EXISTS raw_blocks (
  height BIGINT PRIMARY KEY,
  created_at TIMESTAMP WITH TIME ZONE,
  data JSONB
);

CREATE TABLE IF NOT EXISTS raw_transactions (
  hash text PRIMARY KEY,
  block_height BIGINT REFERENCES raw_blocks(height),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  mined_at TIMESTAMP WITH TIME ZONE,
  dropped_at TIMESTAMP WITH TIME ZONE,
  data JSONB
);

CREATE TABLE IF NOT EXISTS processed_transactions (
  hash TEXT PRIMARY KEY,
  type TEXT,
  block_height BIGINT REFERENCES raw_blocks(height),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  mined_at TIMESTAMP WITH TIME ZONE,
  dropped_at TIMESTAMP WITH TIME ZONE,
  block_hash TEXT,
  sender TEXT,
  gas BIGINT,
  gas_price BIGINT,
  max_fee_per_gas BIGINT,
  max_priority_fee_per_gas BIGINT,
  input TEXT,
  nonce BIGINT,
  receiver TEXT,
  transaction_index BIGINT,
  value BIGINT
);

