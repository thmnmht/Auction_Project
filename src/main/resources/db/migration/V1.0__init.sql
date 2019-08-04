CREATE TABLE Categories
(
    id   INT         NOT NULL AUTO_INCREMENT,
    name VARCHAR(40) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Pictures
(
    id       INT          NOT NULL AUTO_INCREMENT,
    filename VARCHAR(100) NOT NULL,
    date     DATE         NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Bids
(
    id          INT  NOT NULL AUTO_INCREMENT,
    auction_uid INT  NOT NULL,
    user_uid    INT  NOT NULL,
    price       INT  NOT NULL,
    date        DATE NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE Auctions
(
    id           INT         NOT NULL AUTO_INCREMENT,
    title        VARCHAR(50) NOT NULL,
    description  VARCHAR(1000),
    base_price   INT         NOT NULL,
    category_uid INT         NOT NULL,
    date         DATE        NOT NULL,
    state        VARCHAR(20) NOT NULL,
    winner       INT,
    picture_uid  INT         NOT NULL,
    owner        INT         NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Login_infos
(
    id       INT  NOT NULL AUTO_INCREMENT,
    user_uid INT  NOT NULL,
    date     DATE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Users
(
    id        INT          NOT NULL AUTO_INCREMENT,
    name      VARCHAR(40)  NOT NULL,
    email     VARCHAR(40)  NOT NULL,
    password  VARCHAR(100) NOT NULL,
    picture   VARCHAR(100),
    bookmarks INT,
    PRIMARY KEY (id)
);