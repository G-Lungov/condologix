USE `condologix`;

CREATE TABLE CONDOMINIUM (
    ID_CONDOMINIUM INT AUTO_INCREMENT PRIMARY KEY,
    COND_NAME VARCHAR(50) NOT NULL,
    COND_CONTACT NUMERIC(16, 0) NOT NULL,
    AD_AVENUE_STREET_ROAD VARCHAR(100) NOT NULL,
    AD_KILOMETER NUMERIC(5, 0),
    AD_NUMBER NUMERIC (5, 0) NOT NULL,
    AD_POSTAL_CODE NUMERIC(8, 0) NOT NULL,
    AD_NEIGHBORHOOD VARCHAR(50) NOT NULL,
    AD_CITY VARCHAR(50) NOT NULL,
    AD_STATE VARCHAR(30) NOT NULL
);

CREATE TABLE TERMINAL (
	ID_TERMINAL INT AUTO_INCREMENT PRIMARY KEY,
    TEMRINAL_BLOCK VARCHAR(20) NOT NULL,
    TERMINAL_NUMBER NUMERIC(3, 0) NOT NULL,
    ID_CONDOMINIUM INT
);

CREATE TABLE RESIDENT (
	ID_RESIDENT INT AUTO_INCREMENT PRIMARY KEY,
    RESIDENT_NAME VARCHAR(50) NOT NULL,
    RESIDENT_CONTACT NUMERIC(16, 0) NOT NULL,
    ID_TERMINAL INT
);

CREATE TABLE CONCIERGE (
	ID_CONCIERGE INT AUTO_INCREMENT PRIMARY KEY,
    CONCIERGE_NAME VARCHAR(50) NOT NULL,
    CONCIERGE_CONTACT NUMERIC(16, 0) NOT NULL,
    ID_CONDOMINIUM INT
);

CREATE TABLE PACKAGE (
	ID_PACKAGE INT AUTO_INCREMENT PRIMARY KEY,
    PACKAGE_SENDER_NAME VARCHAR (100),
    PACKAGE_ARRIVAL_DATE DATETIME NOT NULL,
    PACKAGE_PICKUP_DATE DATETIME NOT NULL,
    PACKAGE_SCAN BOOLEAN NOT NULL DEFAULT FALSE,
    ID_TERMINAL INT,
    ID_RESIDENT INT,
    ID_CONCIERGE INT
);

CREATE TABLE NOTIFICATION (
	ID_NOTIFICATION INT AUTO_INCREMENT PRIMARY KEY,
    NOTIFICATION_DATE DATETIME NOT NULL,
    ID_PACKAGE INT
);

CREATE TABLE USERS(
	ID_USER INT AUTO_INCREMENT PRIMARY KEY,
    USER_CONTACT NUMERIC (16, 0) NOT NULL,
    USER_PASSWORD VARCHAR(100) NOT NULL
);
