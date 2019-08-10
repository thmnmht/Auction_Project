Auction Project APIs

-/auctions/add  POST : add a new Auction
        String title, String description, int base_price, long date, int category_id, int max_number, MultipartFile[] images


-/users/signup  POST : sign up
        UserDomain : {
            name, email, password
        }

-/auctions/search/{title} GET : search by title
        title : String

-/auctions/filter/{category_id} GET : filter some categories
        category_id : int

-/auctions/homepage GET : to receive auctions (number of pages start with zero)
        @RequestParam("page") int page, @RequestParam("size") int size

-/auctions/all GET : to receive all auctions