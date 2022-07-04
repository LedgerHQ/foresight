CREATE TABLE raw_blocks (
  height BIGINT PRIMARY KEY,
  created_at TIMESTAMP WITH TIME ZONE,
  data JSONB
);

CREATE TABLE raw_transactions (
  hash text PRIMARY KEY,
  block_height BIGINT,
  created_at TIMESTAMP WITH TIME ZONE,
  data JSONB
);

