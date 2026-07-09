-- Migration to add status column to maintenance_plans table
ALTER TABLE maintenance_plans ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED';
