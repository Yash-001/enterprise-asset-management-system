-- Migration to add auditing fields to users table
ALTER TABLE users
    ADD COLUMN created_by VARCHAR(100) DEFAULT 'SYSTEM',
    ADD COLUMN updated_by VARCHAR(100) DEFAULT 'SYSTEM';
