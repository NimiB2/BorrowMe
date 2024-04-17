package com.project1.borrowme.views;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.project1.borrowme.R;
import com.project1.borrowme.Utilities.BorrowUtil;
import com.project1.borrowme.Utilities.FirebaseUtil;
import com.project1.borrowme.Utilities.LocationManagerUtil;
import com.project1.borrowme.Utilities.MySignal;
import com.project1.borrowme.data.CategoriesData;
import com.project1.borrowme.interfaces.CallbackCheckUsers;
import com.project1.borrowme.interfaces.CallbackAddFirebase;
import com.project1.borrowme.interfaces.LocationFetchListener;
import com.project1.borrowme.models.Borrow;
import com.project1.borrowme.models.ReceivedBorrow;
import com.project1.borrowme.models.TheUser;
import com.project1.borrowme.screens.HomeFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class NewBorrowingFragment extends Fragment {
    private final int DISTANCE_1 = 1;
    private final int DISTANCE_5 = 5;
    private final int DISTANCE_10 = 10;
    private TheUser theUser;
    private AppCompatImageButton newBorrow_SIV_back;
    private TextInputEditText newBorrow_ET_itemName;
    private TextInputEditText newBorrow_ET_description;


    private SwitchMaterial newBorrow_SWITCH_changeLocation;
    private SwitchCompat newBorrow_SWITCH_getLocation;
    private MaterialCardView newBorrow_CARD_search;

    private MaterialTextView newBorrow_MTV_categories;
    private ShapeableImageView newBorrow_SIV_categories;
    private MaterialCardView newBorrow_CARD_categories;

    private RadioGroup distance_options_container;
    private MaterialRadioButton newBorrow_RADIO_1km;
    private MaterialRadioButton newBorrow_RADIO_5km;
    private MaterialRadioButton newBorrow_RADIO_10km;
    private MaterialButton newBorrow_BTN_submit;

    private LocationManagerUtil locationManagerUtil;
    private FusedLocationProviderClient fusedLocationClient;

    boolean[] selectedCategories;
    ArrayList<String> categoryList;
    String[] categoryArray;
    private int chosenRadiusKm;
    private double lat;
    private double lon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_borrowing, container, false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());

        initDefaultDetails();
        findViews(view);
        initViews();
        initCategories();
        setRadioGroup(view);
        setupLocationManager();

        return view;
    }

    private void initDefaultDetails() {
        theUser = TheUser.getInstance();
        chosenRadiusKm = DISTANCE_5;

        lat = theUser.getUserDetails().getLat();
        lon = theUser.getUserDetails().getLon();

        categoryList = new ArrayList<>();
    }

    private void initCategories() {
        categoryArray = CategoriesData.getNames();
        selectedCategories = new boolean[categoryArray.length];
        newBorrow_CARD_categories.setOnClickListener(v -> {
            showCategoriesDialog();
        });
    }

    private void showCategoriesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Select Categories");
        builder.setCancelable(false);

        builder.setMultiChoiceItems(categoryArray, selectedCategories, (dialog, which, isChecked) -> {
                    String categoryName = categoryArray[which];
                    if (isChecked) {
                        if (!categoryList.contains(categoryName)) {
                            categoryList.add(categoryName);
                        }
                    } else {
                        categoryList.remove(categoryName);
                    }
                }).setPositiveButton("OK", (dialog, which) -> {
                    String categoriesText = TextUtils.join(", ", categoryList);
                    newBorrow_MTV_categories.setText(categoriesText);
                }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setNeutralButton("Clear all", (dialog, which) -> {
                    Arrays.fill(selectedCategories, false);
                    categoryList.clear();
                    newBorrow_MTV_categories.setText("");
                });

        builder.show();
    }

    private void setRadioGroup(View view) {

        distance_options_container.setOnCheckedChangeListener((group, checkedId) -> {
            // This ensures only one radio button can be active at a time
            if (checkedId == R.id.newBorrow_RADIO_1km) {
                chosenRadiusKm = DISTANCE_1;
                newBorrow_RADIO_1km.setChecked(true);
                newBorrow_RADIO_5km.setChecked(false);
                newBorrow_RADIO_10km.setChecked(false);
            } else if (checkedId == R.id.newBorrow_RADIO_5km) {
                chosenRadiusKm = DISTANCE_5;
                newBorrow_RADIO_1km.setChecked(false);
                newBorrow_RADIO_5km.setChecked(true);
                newBorrow_RADIO_10km.setChecked(false);
            } else if (checkedId == R.id.newBorrow_RADIO_10km) {
                chosenRadiusKm = DISTANCE_10;
                newBorrow_RADIO_1km.setChecked(false);
                newBorrow_RADIO_5km.setChecked(false);
                newBorrow_RADIO_10km.setChecked(true);
            }
        });
    }

    private void setupLocationManager() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.newBorrow_FRAGMENT_autoComplete);
        locationManagerUtil = new LocationManagerUtil(getContext(), autocompleteFragment, new LocationFetchListener() {
            @Override
            public void onLocationFetched(double lat, double lon) {
                setAddress(lat, lon);
            }

            @Override
            public void onLocationFetchFailed() {
                MySignal.getInstance().toast("Failed to fetch location. Please ensure your location services are enabled, or enter a valid address.");
            }
        });
    }

    private void setAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String location = address.getAddressLine(0);
                this.lat = latitude; // set class variable
                this.lon = longitude; // set class variable
            } else {
                throw new IOException("No address found for coordinates: " + latitude + ", " + longitude);
            }
        } catch (IOException e) {
            MySignal.getInstance().toast("Failed to fetch location details.");
            e.printStackTrace();
        }
    }


    private boolean validateInputs() {
        // Validate item name length
        if (newBorrow_ET_itemName.getText().toString().trim().length() < 2) {
            MySignal.getInstance().toast("Item name must be at least 2 characters.");
            return false;
        }

        // Validate that at least one category is selected
        if (categoryList.isEmpty()) {
            MySignal.getInstance().toast("Please select at least one category.");
            return false;
        }

        return true;
    }

    private void getLocation() {

        if (newBorrow_SWITCH_getLocation.isChecked()) {
            // Get current location from GPS
            locationManagerUtil.getCurrentLocation();
        } else {
            // Use the selected location from autocomplete
            locationManagerUtil.triggerLocationUpdate();
        }
    }

    private void toggleLocationVisibility() {
        if (newBorrow_SWITCH_changeLocation.isChecked()) {
            newBorrow_SWITCH_getLocation.setVisibility(View.VISIBLE);
            toggleLocationSwitchVisibility();

        } else {
            newBorrow_SWITCH_getLocation.setVisibility(View.GONE);
            newBorrow_CARD_search.setVisibility(View.GONE);

        }

    }

    private void toggleLocationSwitchVisibility() {
        newBorrow_SWITCH_getLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    newBorrow_CARD_search.setVisibility(View.GONE);
                } else {
                    newBorrow_CARD_search.setVisibility(View.VISIBLE);

                }
            }
        });
    }


    private void backToHome() {
        if (getActivity() != null) {

            Fragment homeFragment = new HomeFragment();
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_FARM_layout, homeFragment).commit();
        }

    }

    private void submitForm() {
        if (validateInputs()) {
            MySignal.getInstance().vibrate(true);
            MySignal.getInstance().toast("Your request has been successfully sent, we will notify you when there are answers");
            ;
            getLocation();
            String myId= FirebaseUtil.currentUserId();
            Borrow newBorrow = new Borrow(
                    theUser.getUserDetails().getuName(),
                    myId,
                    newBorrow_ET_itemName.getText().toString().trim(),
                    newBorrow_ET_description.getText().toString().trim(),
                    categoryList,
                    chosenRadiusKm,
                    lat,
                    lon
            );
            theUser.addBorrow(newBorrow.getId(), newBorrow);
            CallbackCheckUsers checkUsers= new CallbackCheckUsers() {
                @Override
                public void checkUsers(Borrow newBorrow) {
                    addingForMyHistory(myId,newBorrow);
                }
            };

            FirebaseUtil.addBorrowToFirestore(newBorrow,checkUsers);

        }

    }

    private void addingForMyHistory(String myId, Borrow newBorrow) {
        ReceivedBorrow receivedBorrow = new ReceivedBorrow(newBorrow,theUser.getUid(),true);
        theUser.addToMap(theUser.getHistory(),receivedBorrow.getId(),receivedBorrow);
        CallbackAddFirebase callbackAddFirebase = new CallbackAddFirebase() {
            @Override
            public void onAddToFirebase(ReceivedBorrow receivedBorrow) {

                checkOtherUsers(myId,newBorrow);
                backToHome();
            }
        };
        FirebaseUtil.addReceivedBorrowToFirestore(receivedBorrow,"history", callbackAddFirebase,myId);

    }


    private void checkOtherUsers(String senderId,Borrow newBorrow) {
        BorrowUtil.findEligibleUsers(senderId,newBorrow.getLat(), newBorrow.getLon(), newBorrow.getRadiusKm(),newBorrow.getCategories()).thenAccept(otherUsersId->{
            sendGetBorrow(otherUsersId,newBorrow);
        });
    }

    private void sendGetBorrow(List<String> otherUsersId,Borrow newBorrow) {
        for (String userId :otherUsersId) {
            ReceivedBorrow receivedBorrow = new ReceivedBorrow(newBorrow,userId,false );
            CallbackAddFirebase callbackAddFirebase = new CallbackAddFirebase() {
                @Override
                public void onAddToFirebase(ReceivedBorrow receivedBorrow) {
                    newBorrow.updateNumOfSending();
                    addForMessages(receivedBorrow,userId);

                }
            };
            FirebaseUtil.addReceivedBorrowToFirestore(receivedBorrow,"receivedBorrowMap", callbackAddFirebase,userId);
        }

    }

    private void addForMessages(ReceivedBorrow receivedBorrow,String userId) {
        CallbackAddFirebase callbackAddFirebase = new CallbackAddFirebase() {
            @Override
            public void onAddToFirebase(ReceivedBorrow receivedBorrow) {

            }
        };
        FirebaseUtil.addReceivedBorrowToFirestore(receivedBorrow,"Messages", callbackAddFirebase,userId);
    }


    private void initViews() {
        newBorrow_SIV_back.setOnClickListener(v -> backToHome());
        newBorrow_BTN_submit.setOnClickListener(v -> submitForm());
        newBorrow_SWITCH_getLocation.setOnClickListener(v -> getLocation());
        newBorrow_SWITCH_changeLocation.setOnClickListener(v -> toggleLocationVisibility());

    }

    private void findViews(View view) {
        newBorrow_SIV_back = view.findViewById(R.id.newBorrow_SIV_back);
        newBorrow_ET_itemName = view.findViewById(R.id.newBorrow_ET_itemName);
        newBorrow_ET_description = view.findViewById(R.id.newBorrow_ET_description);

        newBorrow_SIV_categories = view.findViewById(R.id.newBorrow_SIV_categories);
        newBorrow_CARD_categories = view.findViewById(R.id.newBorrow_CARD_categories);
        newBorrow_MTV_categories = view.findViewById(R.id.newBorrow_MTV_categories);

        newBorrow_SWITCH_changeLocation = view.findViewById(R.id.newBorrow_SWITCH_changeLocation);
        newBorrow_SWITCH_getLocation = view.findViewById(R.id.newBorrow_SWITCH_getLocation);
        newBorrow_CARD_search = view.findViewById(R.id.newBorrow_CARD_search);

        distance_options_container = view.findViewById(R.id.distance_options_container);
        newBorrow_RADIO_1km = view.findViewById(R.id.newBorrow_RADIO_1km);
        newBorrow_RADIO_5km = view.findViewById(R.id.newBorrow_RADIO_5km);
        newBorrow_RADIO_10km = view.findViewById(R.id.newBorrow_RADIO_10km);

        newBorrow_BTN_submit = view.findViewById(R.id.newBorrow_BTN_submit);
    }

}