package com.testapp.cryptocompare;

/**
 * Created by jecihjoy on 03/11/2017.
 */

public class CustomArray {
    String title, curr;

    public CustomArray(String title, String curr) {
        this.title = title;
        this.curr = curr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCurr() {
        return curr;
    }

    public void setCurr(String curr) {
        this.curr = curr;
    }
}

