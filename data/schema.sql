
-- USERS TABLE
CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(255),
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255),
  role VARCHAR(50),
  user_image VARCHAR(500),
  suspended BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- VENUES TABLE
CREATE TABLE IF NOT EXISTS venues (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  court_name VARCHAR(255) NOT NULL,
  court_location VARCHAR(255),
  court_image VARCHAR(500),
  sport VARCHAR(100) NOT NULL,
  price INTEGER,
  rating NUMERIC(3, 2),
  courts_available INTEGER,
  game_icon VARCHAR(500),
  created_by UUID,
  disabled BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_venues_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

-- GAMES TABLE
CREATE TABLE IF NOT EXISTS games (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL,
  venue_id UUID NOT NULL,
  date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  skill_level VARCHAR(50),
  status VARCHAR(50) NOT NULL,
  members_joined INTEGER,
  total_members INTEGER,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_games_user_id FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_games_venue_id FOREIGN KEY (venue_id) REFERENCES venues(id)
);

-- BOOKINGS TABLE
CREATE TABLE IF NOT EXISTS bookings (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL,
  venue_id UUID NOT NULL,
  booking_date VARCHAR(255) NOT NULL,
  booking_slot VARCHAR(100) NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  status VARCHAR(50),
  refund_status VARCHAR(50),
  order_id VARCHAR(255),
  payment_intent_id VARCHAR(255),
  refund_id VARCHAR(255),
  refund_requested_at TIMESTAMP WITH TIME ZONE,
  refund_processed_at TIMESTAMP WITH TIME ZONE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_bookings_user_id FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_bookings_venue_id FOREIGN KEY (venue_id) REFERENCES venues(id)
);

-- PAYMENTS TABLE
CREATE TABLE IF NOT EXISTS payments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL,
  venue_id UUID NOT NULL,
  amount BIGINT NOT NULL,
  order_id VARCHAR(255) NOT NULL,
  checkout_session_id VARCHAR(255),
  payment_intent_id VARCHAR(255),
  status VARCHAR(50) NOT NULL,
  currency VARCHAR(10) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  court_name VARCHAR(255) NOT NULL,
  court_location VARCHAR(255) NOT NULL,
  court_image VARCHAR(500),
  booking_date VARCHAR(255) NOT NULL,
  booking_slot VARCHAR(100) NOT NULL,
  sport VARCHAR(100) NOT NULL,
  members_joined INTEGER NOT NULL,
  total_members INTEGER NOT NULL,
  CONSTRAINT fk_payments_user_id FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_payments_venue_id FOREIGN KEY (venue_id) REFERENCES venues(id)
);

-- JOIN REQUESTS TABLE
CREATE TABLE IF NOT EXISTS join_requests (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  game_id UUID NOT NULL,
  sender_id UUID NOT NULL,
  recipient_id UUID NOT NULL,
  status VARCHAR(50),
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_join_requests_game_id FOREIGN KEY (game_id) REFERENCES games(id),
  CONSTRAINT fk_join_requests_sender_id FOREIGN KEY (sender_id) REFERENCES users(id),
  CONSTRAINT fk_join_requests_recipient_id FOREIGN KEY (recipient_id) REFERENCES users(id)
);

-- MESSAGES TABLE
CREATE TABLE IF NOT EXISTS messages (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  sender_id UUID NOT NULL,
  recipient_id UUID NOT NULL,
  content VARCHAR(1000) NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_messages_sender_id FOREIGN KEY (sender_id) REFERENCES users(id),
  CONSTRAINT fk_messages_recipient_id FOREIGN KEY (recipient_id) REFERENCES users(id)
);

-- CREATE INDEXES FOR PERFORMANCE
CREATE INDEX IF NOT EXISTS idx_venues_created_by ON venues(created_by);
CREATE INDEX IF NOT EXISTS idx_venues_sport ON venues(sport);
CREATE INDEX IF NOT EXISTS idx_games_user_id ON games(user_id);
CREATE INDEX IF NOT EXISTS idx_games_venue_id ON games(venue_id);
CREATE INDEX IF NOT EXISTS idx_games_date ON games(date);
CREATE INDEX IF NOT EXISTS idx_bookings_user_id ON bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_bookings_venue_id ON bookings(venue_id);
CREATE INDEX IF NOT EXISTS idx_bookings_status ON bookings(status);
CREATE INDEX IF NOT EXISTS idx_payments_user_id ON payments(user_id);
CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_join_requests_game_id ON join_requests(game_id);
CREATE INDEX IF NOT EXISTS idx_join_requests_sender_id ON join_requests(sender_id);
CREATE INDEX IF NOT EXISTS idx_join_requests_status ON join_requests(status);
CREATE INDEX IF NOT EXISTS idx_messages_sender_id ON messages(sender_id);
