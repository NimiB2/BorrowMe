package com.project1.borrowme.interfaces;

public interface LocationFetchListener {
    void onLocationFetched(double latitude, double longitude);
    void onLocationFetchFailed();
}
