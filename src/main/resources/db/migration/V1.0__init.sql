CREATE TABLE Categories
(
    id   INT         NOT NULL AUTO_INCREMENT,
    name VARCHAR(40) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



CREATE TABLE Users
(
    id        INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name      VARCHAR(40)  NOT NULL,
    email     VARCHAR(40)  NOT NULL,
    password  VARCHAR(100) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

  CREATE TABLE User_Pictures
(
    id       INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id  INT          NOT NULL,
    date     DATE         NOT NULL,
    FOREIGN KEY (user_id)
        REFERENCES Users (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



CREATE TABLE Auctions
(
    id           INT         NOT NULL AUTO_INCREMENT,
    title        VARCHAR(50) NOT NULL,
    description  VARCHAR(1000),
    base_price   INT         NOT NULL,
    category_id INT         NOT NULL,
    date         DATE        NOT NULL,
    state        INT         NOT NULL,
    winner_id       INT      NOT NULL,
    owner_id        INT         NOT NULL,
    max_number  INT        NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (winner_id)
        REFERENCES Users (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (owner_id)
        REFERENCES Users (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (category_id)
        REFERENCES Categories (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

  CREATE TABLE Auction_Pictures
(
    id       INT          NOT NULL AUTO_INCREMENT,
    Auction_id  INT          NOT NULL,
    date     DATE         NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (Auction_id)
        REFERENCES Auctions (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE Login_infos
(
    id       INT  NOT NULL AUTO_INCREMENT,
    user_id INT  NOT NULL,
    date     DATE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES Users (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE Bids
(
    id          INT  NOT NULL AUTO_INCREMENT,
    auction_id INT  NOT NULL,
    user_id    INT  NOT NULL,
    price       INT  NOT NULL,
    date        DATE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES Users (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (auction_id)
        REFERENCES Auctions (id)
        ON UPDATE CASCADE ON DELETE RESTRICT

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

  ALTER TABLE  Users ADD COLUMN picture_id INT, ADD
    FOREIGN KEY (picture_id)
    REFERENCES User_Pictures (id)
    ON UPDATE CASCADE ON DELETE RESTRICT
