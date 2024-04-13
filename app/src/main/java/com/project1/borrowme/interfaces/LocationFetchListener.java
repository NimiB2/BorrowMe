package com.project1.borrowme.interfaces;

import java.io.IOException;

public interface LocationFetchListener {
    void onLocationFetched(double latitude, double longitude) throws IOException;
    void onLocationFetchFailed();
}
