CREATE TABLE Categories
(
    id   INT         NOT NULL AUTO_INCREMENT,
    name VARCHAR(40) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



CREATE TABLE Users
(
    id       INT          NOT NULL AUTO_INCREMENT,
    name     VARCHAR(40)  NOT NULL,
    email    VARCHAR(40)  NOT NULL,
    password VARCHAR(100) NOT NULL,
    picture  VARCHAR(100),
    PRIMARY KEY (id),
    UNIQUE KEY unique_email (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE Auctions
(
    id          INT         NOT NULL AUTO_INCREMENT,
    title       VARCHAR(50) NOT NULL,
    description VARCHAR(1000),
    base_price  INT         NOT NULL,
    category_id INT         NOT NULL,
    date        TIMESTAMP    NOT NULL,
    state       INT         NOT NULL,
    max_number   INT         NOT NULL,
    winner_id   INT,
    owner_id    INT         NOT NULL,
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


CREATE TABLE Pictures
(
    id         INT          NOT NULL AUTO_INCREMENT,
    filename   VARCHAR(100) NOT NULL,
    auction_id INT          NOT NULL,
    date       TIMESTAMP     NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (auction_id)
        REFERENCES Auctions (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE Login_infos
(
    id      INT      NOT NULL AUTO_INCREMENT,
    user_id INT      NOT NULL,
    date    TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES Users (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE Bids
(
    id         INT      NOT NULL AUTO_INCREMENT,
    auction_id INT      NOT NULL,
    user_id    INT      NOT NULL,
    price      INT      NOT NULL,
    date       TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES Users (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (auction_id)
        REFERENCES Auctions (id)
        ON UPDATE CASCADE ON DELETE RESTRICT

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;