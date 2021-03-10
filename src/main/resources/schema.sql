-- Create new account tables if not exists
CREATE TABLE IF NOT EXISTS `account` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `uuid` binary(16) NOT NULL,
   `name` varchar(16) NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `uuid` (`uuid`)
);

-- Create new currency account tables if not exists
CREATE TABLE IF NOT EXISTS `currency_account` (
    `account_id` int(11) NOT NULL,
    `amount` double NOT NULL DEFAULT 0.0,
    PRIMARY KEY (`account_id`),
    FOREIGN KEY (`account_id`) REFERENCES `account`(`id`) ON DELETE CASCADE
);
