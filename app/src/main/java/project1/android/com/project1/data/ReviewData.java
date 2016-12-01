package project1.android.com.project1.data;

import java.util.ArrayList;

/**
 * Created by ruchi on 22/11/16.
 */
public class ReviewData extends Data{
    private String id;
    private String page;

    public ArrayList<ReviewResult> getResults() {
        return results;
    }

    public void setResults(ArrayList<ReviewResult> results) {
        this.results = results;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private ArrayList<ReviewResult> results;
}
