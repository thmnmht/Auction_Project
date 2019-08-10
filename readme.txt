Auction Project APIs




header "Auth" : "Bearer 'Token'"


-/login : login  POST
        email : String
        password : String

-/users/signup  POST : sign up
        UserDomain : {
            name,
            email,
            password
        }

        if password < 6 || password > 100 : "status": 400 Bad Request
        if name < 1 || name > 100 :   "status": 400 Bad Request
        if email isn't valid : "status": 400 Bad Request
        if email duplicated :  "status": 500 Internal Server Error

        response => Resource<User> : User {
                name,email,picture,bookmarkes
        }


-/auctions/add  POST : add a new Auction
        String title, String description, int base_price, long date, int category_id, int max_number, MultipartFile[] images

        if title.length < 1 || title.length > 100 : "status": 400 Bad Request
        if base_price < 1 : "status": 400 Bad Request
        if description > 2000 : "status": 500 Internal Server Error
        if category_id doesn't exist :  "status": 500 Internal Server Error
        if date == null : "status": 400 Bad Request
        if max_number  < 2 || max_number > 15 : "status": 400 Bad Request
        if size of image is too high : "status": 500 Internal Server Error

        response => Resource<AuctionDomain> : AuctionDomain{
                   title,description,base_price,date,category_id,max_number,pictures,state
        }

-/auctions/search/{title} GET : search by title
        title : String

-/auctions/filter/{category_id} GET : filter some categories
        category_id : int

-/auctions/homepage GET : to receive auctions (number of pages start with zero)
        @RequestParam("page") int page, @RequestParam("size") int size

-/auctions/all GET : to receive all auctions