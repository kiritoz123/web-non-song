-- init.sql: tạo schema và các bảng chính cho project Nếp Gấp Non Sông (nssdb)
-- LƯU Ý: không chèn mật khẩu admin ở đây (mật khẩu cần được băm), DataInitializer trong app sẽ tạo account admin.

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET NAMES utf8mb4;
SET time_zone = '+00:00';

-- Table roles
CREATE TABLE IF NOT EXISTS roles (
                                     id BIGINT NOT NULL AUTO_INCREMENT,
                                     name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_roles_name (name)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table users
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT NOT NULL AUTO_INCREMENT,
                                     email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    phone VARCHAR(50),
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Join table user_roles (many-to-many)
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role_id BIGINT NOT NULL,
                                          PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Products
CREATE TABLE IF NOT EXISTS products (
                                        id BIGINT NOT NULL AUTO_INCREMENT,
                                        name VARCHAR(512) NOT NULL,
    description TEXT,
    price DECIMAL(18,2),
    sku VARCHAR(100),
    locale VARCHAR(10),
    stock INT DEFAULT 0,
    status VARCHAR(50),
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Product images: simple table to store multiple images per product
CREATE TABLE IF NOT EXISTS product_images (
                                              id BIGINT NOT NULL AUTO_INCREMENT,
                                              product_id BIGINT NOT NULL,
                                              url VARCHAR(1000),
    PRIMARY KEY (id),
    CONSTRAINT fk_prod_img_prod FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Articles
CREATE TABLE IF NOT EXISTS articles (
                                        id BIGINT NOT NULL AUTO_INCREMENT,
                                        title VARCHAR(1000),
    content LONGTEXT,
    author VARCHAR(255),
    published_at DATETIME,
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Subscriptions (newsletter)
CREATE TABLE IF NOT EXISTS subscriptions (
                                             id BIGINT NOT NULL AUTO_INCREMENT,
                                             email VARCHAR(255),
    locale VARCHAR(10),
    subscribed_at DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY uq_sub_email (email)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Cart and cart_items
CREATE TABLE IF NOT EXISTS carts (
                                     id BIGINT NOT NULL AUTO_INCREMENT,
                                     user_id BIGINT,
                                     PRIMARY KEY (id),
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cart_items (
                                          id BIGINT NOT NULL AUTO_INCREMENT,
                                          cart_id BIGINT,
                                          product_id BIGINT,
                                          quantity INT,
                                          PRIMARY KEY (id),
    CONSTRAINT fk_cartitem_cart FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_cartitem_prod FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Payment transactions
CREATE TABLE IF NOT EXISTS payment_transactions (
                                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                                    order_id BIGINT,
                                                    provider VARCHAR(50),
    external_id VARCHAR(255),
    amount DECIMAL(18,2),
    currency VARCHAR(10),
    status VARCHAR(50),
    user_id BIGINT,
    created_at DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3D models
CREATE TABLE IF NOT EXISTS models_3d (
                                         id BIGINT NOT NULL AUTO_INCREMENT,
                                         title VARCHAR(512),
    description TEXT,
    asset_url VARCHAR(1000),
    thumbnail_url VARCHAR(1000),
    locale VARCHAR(10),
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Itineraries and markers
CREATE TABLE IF NOT EXISTS itineraries (
                                           id BIGINT NOT NULL AUTO_INCREMENT,
                                           user_id BIGINT,
                                           title VARCHAR(512),
    description TEXT,
    created_at DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_itinerary_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS itinerary_markers (
                                                 id BIGINT NOT NULL AUTO_INCREMENT,
                                                 itinerary_id BIGINT,
                                                 title VARCHAR(255),
    description TEXT,
    lat DOUBLE,
    lon DOUBLE,
    PRIMARY KEY (id),
    CONSTRAINT fk_marker_itinerary FOREIGN KEY (itinerary_id) REFERENCES itineraries(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS itinerary_marker_media (
                                                      id BIGINT NOT NULL AUTO_INCREMENT,
                                                      marker_id BIGINT,
                                                      url VARCHAR(1000),
    PRIMARY KEY (id),
    CONSTRAINT fk_marker_media_marker FOREIGN KEY (marker_id) REFERENCES itinerary_markers(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Feedback
CREATE TABLE IF NOT EXISTS feedbacks (
                                         id BIGINT NOT NULL AUTO_INCREMENT,
                                         user_id BIGINT,
                                         product_id BIGINT,
                                         message TEXT,
                                         rating INT,
                                         created_at DATETIME,
                                         PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Returns / refunds
CREATE TABLE IF NOT EXISTS return_requests (
                                               id BIGINT NOT NULL AUTO_INCREMENT,
                                               order_id BIGINT,
                                               user_id BIGINT,
                                               reason TEXT,
                                               account_info VARCHAR(1000),
    status VARCHAR(50),
    admin_comment TEXT,
    refund_receipt_url VARCHAR(1000),
    created_at DATETIME,
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Orders and items
CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT NOT NULL AUTO_INCREMENT,
                                      user_id BIGINT,
                                      total DECIMAL(18,2),
    status VARCHAR(50),
    created_at DATETIME,
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS order_items (
                                           id BIGINT NOT NULL AUTO_INCREMENT,
                                           order_id BIGINT,
                                           product_id BIGINT,
                                           quantity INT,
                                           price DECIMAL(18,2),
    PRIMARY KEY (id),
    CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Optional: add basic indices for search
CREATE INDEX IF NOT EXISTS idx_products_name ON products (name(100));
CREATE INDEX IF NOT EXISTS idx_articles_title ON articles (title(200));

-- Insert default roles (if not present)
INSERT INTO roles (name)
SELECT 'ROLE_USER' UNION SELECT 'ROLE_MANAGER' UNION SELECT 'ROLE_ADMIN'
    ON DUPLICATE KEY UPDATE name = name;