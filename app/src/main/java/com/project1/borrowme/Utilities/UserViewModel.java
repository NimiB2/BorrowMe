package com.project1.borrowme.Utilities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project1.borrowme.models.TheUser;

public class UserViewModel extends ViewModel {
    private MutableLiveData<TheUser> userLiveData = new MutableLiveData<>();

    public LiveData<TheUser> getUserLiveData() {
        loadUserData();
        return userLiveData;
    }

    private void loadUserData() {
        FirebaseUtil.currentUserFirestore().get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null) {
                userLiveData.postValue(task.getResult().toObject(TheUser.class));
            } else {
                userLiveData.postValue(null);
            }
        });
    }
}

