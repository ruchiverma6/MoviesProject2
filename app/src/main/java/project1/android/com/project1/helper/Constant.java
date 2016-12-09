package project1.android.com.project1.helper;

/**
 * Created by ruchi on 31/10/16.
 */
public class Constant {

    public static final int MOVIE_TYPE =0 ;
    public static final int MOVIE_DETAIL_TYPE =1 ;
    public static final int TRAILERS_TYPE =2 ;
    public static final int REVIEW_TYPE =3 ;
    public static final String RESPONSE_DATA = "responsedata";
    public static final String RESPONSE_CODE = "responsecode";
    public static final int NO_INTERNET_CONNECTION_STATUS = 123;
    public static final String RESULT_DATA_ARRAY = "resultdatalist";
    public static final String MOVIE_DETAIL_DATA ="moviedetaildata" ;
    public static final java.lang.String MOVIE_ID_KEY = "movieid";
    public static final String DATA_TYPE = "datatype";
    //Reference variable to hold popular movies url
    public static String POPULAR_MOVIES_URL="http://api.themoviedb.org/3/movie/%s?api_key=%s";


    //Reference variable to hold top rated movies url
    public static String TOP_RATED_URL="http://api.themoviedb.org/3/movie/top_rated?api_key=%s";

    //Reference variable to hold Movie DB Api key.
    public static String MOVIE_DB_API_KEY="f6ffadde87fb81871dfed27ac7dd61af";  //Need to add Movie Db API key for working of this project.

    //Movie poster base url;
    public static String MOVIE_POSTER_BASE_URL="http://image.tmdb.org/t/p/%s/";

   //Movie poster image width
    public static String MOVIE_POSTER_IMAGE_WIDTH="w185";


    public static String movie_id_key ="movie_id_key";

    public static String DATE_FORMAT_PATTERN="yyyy";

    public static String MOVIE_DETAIL_URL="http://api.themoviedb.org/3/movie/%s?api_key=%s";

    public static String TRAILERS_URL="https://api.themoviedb.org/3/movie/%s/videos?api_key=%s&language=en-US";


    public static String REVIEW_URL="https://api.themoviedb.org/3/movie/%s/reviews?api_key=%s&language=en-US";

    //https://api.themoviedb.org/3/movie/{movie_id}/videos?api_key=<<api_key>>&language=en-US
    public static int NO_INTERNET_CONNECTION=123;
 public static final String DEVELOPER_KEY="AIzaSyDOltNO56-dB7bOWqqlEt1NrSRF2s8q-CI\t";

}
