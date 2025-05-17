-- Create database
CREATE DATABASE IF NOT EXISTS weather_app;
USE weather_app;

-- Table for users (used in Login & Sign-Up)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Table for weather search history
CREATE TABLE IF NOT EXISTS weather_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    city VARCHAR(100) NOT NULL,
    temperature FLOAT NOT NULL,
    `condition` VARCHAR(100) NOT NULL,
    search_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
