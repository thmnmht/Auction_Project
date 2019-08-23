# initial users decoded passwords in order : a.dizaji , y.yarandi , o.seyfan , t.mohati , s.ebrahimi
INSERT INTO Users (name, email, password)
VALUES ("علیرضا دیزجی", "Alirezadizaji@yahoo.com", "$2a$10$OraZDACEgxzTyF5jK8XTo.ID/bcMEknBWMi3lXwYcCwIpDMfJndaG");
INSERT INTO Users (name, email, password)
VALUES ("یلدا یارندی", "yalda.yarandi@gmail.com", "$2a$10$NpanvpOxS9tL3hHSy2JKquiA8iFqwhHCmhRPM9K3a.tzxzjRCXCTK");
INSERT INTO Users (name, email, password)
VALUES ("امید سیفان", "seyfanomid@ymail.com", "$2a$10$CDcfMF5fHv5LuczO50wxuOvnMvEiCFLmpJN.WPLHs006aWwpGFWbi");
INSERT INTO Users (name, email, password)
VALUES ("تهمینه محاطی", "tmohati@gmail.com", "$2a$10$DarBf7KvL.QK4/FResFZ.OvYw5NTV0ieuaqq975DSrOmeh/wScckG");
INSERT INTO Users (name, email, password)
VALUES ("سبحان ابراهیمی", "sobhanebrahimi82@gmail.com", "$2a$10$JGM95m2hq9ut0QwcltxvOu29BdWJbbGM2Pe8nRJL1QbHs8Q2jZNSq");


# initial auctions
INSERT INTO Auctions (title, description, base_price, category_id, date, state, owner_id)
VALUES ("توپ بسکتبال نابی", "دست یه بسکتبالیست حرفه‌ای بوده!", 400000,
        (SELECT id FROM Categories WHERE Categories.name = "ورزشی"), '2020-05-27 00:00:00', 0,
        (SELECT id FROM Users WHERE Users.email = "Alirezadizaji@yahoo.com"));
INSERT INTO Auctions (title, description, base_price, category_id, date, state, owner_id)
VALUES ("macbook pro نو", "دست یه برنامه نویس حرفه‌ای بوده!", 20000000,
        (SELECT id FROM Categories WHERE Categories.name = "دیجیتال"), '2019-05-09 12:00:00', 0,
        (SELECT id FROM Users WHERE Users.email = "seyfanomid@ymail.com"));
INSERT INTO Auctions (title, description, base_price, category_id, date, state, owner_id)
VALUES ("Apple Watch", "تمیز و بدون خط و خش", 1000000, (SELECT id FROM Categories WHERE Categories.name = "دیجیتال"),
        '2019-04-09 22:00:00', 0, (SELECT id FROM Users WHERE Users.email = "yalda.yarandi@gmail.com"));
INSERT INTO Auctions (title, description, base_price, category_id, date, state, owner_id)
VALUES ("یخچال سایدبای ساید SNOWA", "با ۱.۵ سال گارانتی اصلی", 12000000,
        (SELECT id FROM Categories WHERE Categories.name = "خانه"), '2019-04-09 21:30:00', 0,
        (SELECT id FROM Users WHERE Users.email = "tmohati@gmail.com"));
INSERT INTO Auctions (title, description, base_price, category_id, date, state, owner_id)
VALUES ("لپتاپ ایسوز FX502VM", "سالم و اصل", 16000000,
        (SELECT id FROM Categories WHERE Categories.name = "دیجیتال"), '2019-04-09 21:40:03', 0,
        (SELECT id FROM Users WHERE Users.email = "sobhanebrahimi82@gmail.com"));
INSERT INTO Auctions (title, description, base_price, category_id, date, state, owner_id)
VALUES ("کولر گازی LG", "تورو خدا بخرید یخ کردیم تو رهنماکالج", 7000000,
        (SELECT id FROM Categories WHERE Categories.name = "خانه"), '2019-04-09 18:40:00', 0,
        (SELECT id FROM Users WHERE Users.email = "sobhanebrahimi82@gmail.com"));
INSERT INTO Auctions (title, description, base_price, category_id, date, state, owner_id)
VALUES ("صندلی های بدون چرخ رهنماکالج", "پدر کمرامون دراومده", 100000,
        (SELECT id FROM Categories WHERE Categories.name = "خانه"), '2019-04-10 05:00:00', 0,
        (SELECT id FROM Users WHERE Users.email = "seyfanomid@ymail.com"));
INSERT INTO Auctions (title, description, base_price, category_id, date, state, owner_id)
VALUES ("اجاق گاز رهنماکالج", "اصلا استفاده نشده و سالم و تمیزه", 2000000,
        (SELECT id FROM Categories WHERE Categories.name = "خانه"), '2019-04-10 16:00:00', 0,
        (SELECT id FROM Users WHERE Users.email = "yalda.yarandi@gmail.com"));
INSERT INTO Auctions (title, description, base_price, category_id, date, state, owner_id)
VALUES ("ست لباس ورزشی", "هدیه گرفتم تا حالا نپوشیدم", 200000,
        (SELECT id FROM Categories WHERE Categories.name = "ورزشی"), '2020-04-11 20:30:00', 0,
        (SELECT id FROM Users WHERE Users.email = "tmohati@gmail.com"));





