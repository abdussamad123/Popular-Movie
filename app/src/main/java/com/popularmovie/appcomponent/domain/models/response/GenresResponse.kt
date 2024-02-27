package com.popularmovie.appcomponent.domain.models.response

data class GenresResponse(val genres : ArrayList<Genre>)

data class Genre(val id : Int, val name : String)

/*{
    "genres": [
        {
            "id": 28,
            "name": "Action"
        }
    ]
}*/