package com.project1.borrowme.interfaces;

import com.project1.borrowme.models.ReceivedBorrow;

public interface CallbackAddFirebase {
    void onAddToFirebase(ReceivedBorrow receivedBorrow);
}
