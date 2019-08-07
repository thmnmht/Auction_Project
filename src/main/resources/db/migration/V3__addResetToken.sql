ALTER TABLE Users
    ADD reset_token varchar(40),
    ADD CONSTRAINT token_unique UNIQUE (reset_token);