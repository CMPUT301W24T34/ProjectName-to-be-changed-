package com.example.swiftcheckin.organizer;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.swiftcheckin.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class EventInfoPage extends AppCompatActivity {


    ListView checkedInList;
    ArrayList<Pair<String, String>> checkedInDataList;
    CheckInArrayAdapter checkInArrayAdapter;
    ListView signedUpList;
    ArrayList<Pair<String, String>> signedUpDataList;
    CheckInArrayAdapter signUpArrayAdapter;

    TextView checkedInButton;
    TextView signedUpButton;

    String eventId;

    FirebaseOrganizer dbOrganizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_information);   // This the xml the activity is connected to.
        View view = getWindow().getDecorView();     // OpenAI, April 4, 2024. ChatGPT. Prompt: how to get the current acitivity view in the activity

        dbOrganizer = new FirebaseOrganizer(getApplicationContext());
        eventId = getIntent().getStringExtra("eventId");

        checkedInButton= findViewById(R.id.organizerEventInfo_CheckedInTitle);
        signedUpButton = findViewById(R.id.organizerEventInfo_SignedUpTitle);

        checkedInList = findViewById(R.id.organizerEventInfo_CheckedInList);
        signedUpList = findViewById(R.id.organizerEventInfo_SignedUpList);

        getEventInformation(view);      // event specific data is fetched

        // check-in and signup details initialization
        checkedInDataList = new ArrayList<>();
        checkInArrayAdapter = new CheckInArrayAdapter(this, checkedInDataList);
        checkedInList.setAdapter(checkInArrayAdapter);

        signedUpDataList = new ArrayList<>();
        signUpArrayAdapter = new CheckInArrayAdapter(this, signedUpDataList);
        signedUpList.setAdapter(signUpArrayAdapter);

        fetchCheckedInDetails();
        fetchSignUpDetails();

        initializeListButton(checkedInButton);
        initializeListButton(signedUpButton);


    }

    private void initializeListButton(TextView view1)
    {
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showList();
            }
        });

    }

    private void showList()
    {
        if(checkedInList.getVisibility() == View.INVISIBLE)
        {
            signedUpList.setVisibility(View.INVISIBLE);
            checkedInList.setVisibility(View.VISIBLE);
            signedUpButton.setBackground(null);
            checkedInButton.setBackgroundResource(R.drawable.grey_circle_background);
        }
        else if(signedUpList.getVisibility() == View.INVISIBLE)
        {
            checkedInList.setVisibility(View.INVISIBLE);
            signedUpList.setVisibility(View.VISIBLE);
            checkedInButton.setBackground(null);
            signedUpButton.setBackgroundResource(R.drawable.grey_circle_background);
        }
    }

    private void getEventInformation(View view)
    {
        dbOrganizer.getEvent(eventId, new FirebaseOrganizer.EventCallback() {
            @Override
            public void onCompleteFetch(Event event) {
                TextView title = view.findViewById(R.id.organizerEventInfo_eventTitle);
                TextView date = view.findViewById(R.id.organizerEventInfo_eventStartDate);
                TextView time = view.findViewById(R.id.organizerEventInfo_eventStartTime);
                TextView description = view.findViewById(R.id.organizerEventInfo_eventDescription);
                ImageView poster = view.findViewById(R.id.organizerEventInfo_eventPoster);

                title.setText(event.getEventTitle() + " - Details");
                date.setText(event.getStartDate());
                time.setText(event.getStartTime());
                description.setText(event.getDescription());
                if(event.getEventImageUrl() == null)
                {
                    poster.setImageResource(R.drawable.test_rect);
                }
                else
                {
                    Glide.with(getApplicationContext()).load(event.getEventImageUrl()).into(poster);
                }

            }

            @Override
            public void onError(String errorMessage) {
                Log.e("event fetch", errorMessage);
            }
        });
    }

    private void fetchCheckedInDetails()
    {
        dbOrganizer.getCheckedInDetails(eventId, "checkedIn", checkedInDataList, new FirebaseOrganizer.getCheckInCallback() {
            @Override
            public void onDataFetched(ArrayList<Pair<String, String>> dataList) {
                for(int i = 0; i < dataList.size(); i++)
                {
                    Pair<String, String> pair = dataList.get(i);
                    String deviceId = pair.first;
                    int index = i;
                    dbOrganizer.getUserName(deviceId, new FirebaseOrganizer.getNameCallBack() {
                        @Override
                        public void onNameFetched(String name) {
                            Pair<String, String> updatedPair = new Pair<>(name, pair.second);
                            dataList.set(index, updatedPair);
                            if (index == dataList.size() - 1) {
                                checkInArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Firebase - checkin", errorMessage);
            }
        });
    }

    private void fetchSignUpDetails()
    {
        dbOrganizer.getCheckedInDetails(eventId, "eventsWithAttendees", signedUpDataList, new FirebaseOrganizer.getCheckInCallback() {
            @Override
            public void onDataFetched(ArrayList<Pair<String, String>> dataList) {
                for(int i = 0; i < dataList.size(); i++)
                {
                    Pair<String, String> pair = dataList.get(i);
                    String deviceId = pair.first;
                    int index = i;
                    dbOrganizer.getUserName(deviceId, new FirebaseOrganizer.getNameCallBack() {
                        @Override
                        public void onNameFetched(String name) {
                            Pair<String, String> updatedPair = new Pair<>(name, pair.second);
                            dataList.set(index, updatedPair);
                            if (index == dataList.size() - 1) {
                                signUpArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Error", errorMessage);
            }
        });
    }
}
