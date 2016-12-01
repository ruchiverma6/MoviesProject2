package project1.android.com.project1.data;

import java.util.ArrayList;

/**
 * Created by ruchi on 22/11/16.
 */
public class TrailersData extends Data{
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




    public ArrayList<TrailerResult> getmTrailerResults() {
        return results;
    }

    public void setmTrailerResults(ArrayList<TrailerResult> mTrailerResults) {
        this.results = mTrailerResults;
    }

    private String id;
    private ArrayList<TrailerResult> results;

}
