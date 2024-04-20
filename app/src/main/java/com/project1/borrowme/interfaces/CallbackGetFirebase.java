package com.project1.borrowme.interfaces;

import com.project1.borrowme.models.ReceivedBorrow;

public interface CallbackGetFirebase<T> {
    void onGetFromFirebase(ReceivedBorrow receivedBorrow);
    void onFailure(Exception e);
}

