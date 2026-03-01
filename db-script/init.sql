-- Database: data_magic
-- MySQL Database Design for Data Transformation Application

CREATE DATABASE IF NOT EXISTS data_magic 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE data_magic;

-- Table 1: System Configuration
-- Stores scan directory path and FTP server configuration
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value VARCHAR(500) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table 2: Transformation Rule
-- Stores transformation rules for _MASK1, _MASK2, _MASK3 suffixes
CREATE TABLE IF NOT EXISTS transformation_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_code VARCHAR(50) NOT NULL UNIQUE,
    rule_pattern VARCHAR(100) NOT NULL,
    transformation_type VARCHAR(50) NOT NULL,
    transformation_params VARCHAR(500),
    is_active TINYINT(1) DEFAULT 1,
    priority INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_rule_pattern (rule_pattern),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table 3: Processing Record
-- Main table to record file transformation operations
CREATE TABLE IF NOT EXISTS processing_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_file_name VARCHAR(255) NOT NULL,
    source_file_path VARCHAR(500) NOT NULL,
    target_file_name VARCHAR(255),
    target_file_path VARCHAR(500),
    total_record_count INT DEFAULT 0,
    transformed_field_count INT DEFAULT 0,
    status ENUM('pending', 'processing', 'completed', 'failed', 'uploaded') DEFAULT 'pending',
    error_message TEXT,
    start_time DATETIME NOT NULL,
    complete_time DATETIME,
    ftp_uploaded TINYINT(1) DEFAULT 0,
    ftp_upload_time DATETIME,
    source_deleted TINYINT(1) DEFAULT 0,
    source_delete_time DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    INDEX idx_source_file_name (source_file_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table 4: Field Transformation Log
-- Detailed log of each field transformation
CREATE TABLE IF NOT EXISTS field_transformation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    processing_record_id BIGINT NOT NULL,
    line_number INT NOT NULL,
    field_index INT NOT NULL,
    field_name VARCHAR(100),
    original_value VARCHAR(500),
    transformed_value VARCHAR(500),
    rule_id BIGINT,
    rule_code VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (processing_record_id) REFERENCES processing_record(id) ON DELETE CASCADE,
    INDEX idx_processing_record (processing_record_id),
    INDEX idx_line_number (line_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default transformation rules
INSERT INTO transformation_rule (rule_code, rule_pattern, transformation_type, transformation_params, priority) VALUES
('MASK1', '_MASK1', 'hash', '{"algorithm":"SHA256","length":16}', 1),
('MASK2', '_MASK2', 'mask', '{"char":"*","showLast":4}', 2),
('MASK3', '_MASK3', 'encrypt', '{"algorithm":"AES","keyIndex":1}', 3),
('MASK4', '_MASK4', 'random', '{"type":"alphanumeric","length":8}', 4),
('MASK5', '_MASK5', 'hash', '{"algorithm":"MD5","length":12}', 5);

-- Insert default system configuration
INSERT INTO system_config (config_key, config_value, description) VALUES
('scan_directory', '/data/input', 'Directory to scan for source files'),
('output_directory', '/data/output', 'Directory for transformed output files'),
('ftp_host', 'ftp.example.com', 'FTP server hostname'),
('ftp_port', '21', 'FTP server port'),
('ftp_username', '', 'FTP server username'),
('ftp_password', '', 'FTP server password'),
('ftp_remote_path', '/upload', 'FTP remote upload path'),
('scan_interval_seconds', '60', 'Interval in seconds to scan for new files'),
('field_separator', 'char(27)', 'Field separator character in source files');
