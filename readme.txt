Auction Project APIs

-/auctions/add : add a new Auction
        String title, String description, int base_price, long date, int category_id, int max_number, MultipartFile[] images


-/users/signup : sign up
        UserDomain : {
            name, email, password
        }