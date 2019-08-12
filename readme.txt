Auction Project APIs


header "auth" : "Bearer 'Token'"


************************************************************************************************************************

-/auctions/all GET : to receive all auctions

************************************************************************************************************************

-/auctions/category   GET   : get categories

************************************************************************************************************************

-/auctions/find/{id}    GET : get an auction

************************************************************************************************************************

-/auctions/add  POST : add a new Auction
        String title, String description, int base_price, long date, int category_id, int max_number, MultipartFile[] images


        430 : if title.length < 1 || title==null
        451 : if title.length > 50
        432 : if base_price < 0
        452 : if description > 1000
        436 : if category doesn't exist
        438 : if date contains unsupported value
        437 : if date is too soon (less than half an hour)
        434 : if max_number  < 2
        435 : if max_number > 15
        453 : if size of image is too high (>300MB)

        response => Resource<AuctionDomain> : AuctionDomain{
                   title,description,base_price,date,category_id,max_number,pictures,state
        }

************************************************************************************************************************
************************************************************************************************************************

-/users/login : login  POST
        email : String
        password : String

************************************************************************************************************************

-/users/signup  POST : sign up
        UserDomain : {
            name,
            email,
            password
        }

        441 : if password < 6
        442 : if password > 100
        440 : if name < 1
        443 : if email isn't valid
        454 : if email is duplicated

        response => Resource<User> : User {
                name,email,picture,bookmarkes
        }

************************************************************************************************************************

-/users/edit  POST : edit name and email
              name : String     email : String

************************************************************************************************************************

-/users/me  GET : get details of current user
                  name,email,id,picture

************************************************************************************************************************
************************************************************************************************************************

-/home/search/{title} GET : search by title
        String title, @PathParam("category") int[] categories_id, @RequestParam("page") int page, @RequestParam("size") int size
        if category was empty it search with all categories :)

************************************************************************************************************************

-/home/filter GET : filter some categories
        @PathParam("category") int[] categories_id, @RequestParam("page") int page, @RequestParam("size") int size

************************************************************************************************************************

-/home/all GET : to receive auctions (number of pages start with zero)
        @RequestParam("page") int page, @RequestParam("size") int size

************************************************************************************************************************

-/home/hottest  GET : to receive sorted auctions (number of pages start with zero)
                             @RequestParam("page") int page, @RequestParam("size") int size

************************************************************************************************************************
************************************************************************************************************************

#// Password Recovery

-/forgot POST:
    @Param ("email") : string

    out : Resource<User>
    407 if not found email address

-/reset GET:
     @Param ("token") String

     out : Resource<User>
     448 if reset link is invalid

-/reset POST:
    @Param ("token") String
    @Param ("password") String

    out : Resource<User>
    449 if token not found
    450 if request hasn't been recorded


-users/reset POST:
    @Param ("password") String

    out : Resource<UserDomain>
    445 if password = null | !(5<password.length<100)

