Auction Project APIs

-/login : login
        email : String
        password : String

-/users/signup : sign up
        UserDomain : {
            name, 
            email,
            password
        }

-/auctions/add : add a new Auction
        String title,
        String description,
        int base_price,
        long date, int category_id,
        int max_number,
        MultipartFile[] images


-/auctions/search/{title} : search by title
        title : String

-/auctions/filter/{category_id} : filter some categories
        category_id : int