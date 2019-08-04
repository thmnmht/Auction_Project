CREATE TABLE Categories
(
    id   INT         NOT NULL AUTO_INCREMENT,
    name VARCHAR(40) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Pictures
(
    id      INT          NOT NULL AUTO_INCREMENT,
    filename VARCHAR(100) NOT NULL,
    date    DATE,
    PRIMARY KEY (id)
);