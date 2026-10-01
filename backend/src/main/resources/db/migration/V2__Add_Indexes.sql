-- Create indexes for better query performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at DESC);

CREATE INDEX idx_ads_user_id ON classified_ads(user_id);
CREATE INDEX idx_ads_status ON classified_ads(status);
CREATE INDEX idx_ads_created_at ON classified_ads(created_at DESC);
CREATE INDEX idx_ads_location ON classified_ads(latitude, longitude);
CREATE INDEX idx_ads_category ON classified_ads(category);

CREATE INDEX idx_ad_images_ad_id ON ad_images(ad_id);

-- Update schema_version
INSERT INTO schema_version (version, description, type, script, installed_by, success)
VALUES (2, 'Add Indexes', 'SQL', 'V2__Add_Indexes.sql', 'system', true);
