package com.example.to_donote;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements card_adapter.OnTaskCardClickListener {

    private List<task_card> cardTaskList;
    private RecyclerView recyclerView;
    private card_adapter cardAdapter;

    private Button markDone;
    private ImageButton add_new, share;
    private String launchDateTime;

    private ActivityResultLauncher<Intent> createTaskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String title = data.getStringExtra("title");
                        String description = data.getStringExtra("description");
                        int position = data.getIntExtra("position", -1);
                        String dateTime = data.getStringExtra("dateTime");

                        if (position != -1 && position < cardTaskList.size()) {
                            // If the position is valid, update the existing task_card with the new data
                            task_card taskCard = cardTaskList.get(position);
                            taskCard.setTitle(title);
                            taskCard.setDescription(description);
                            taskCard.setDateTime(dateTime);
                            cardAdapter.notifyDataSetChanged();
                        } else {
                            // If the position is invalid, it means this is a new task_card
                            // Add the data to the ArrayList
                            cardTaskList.add(new task_card(title, description, dateTime));
                            cardAdapter.notifyDataSetChanged();
                        }

                        // Save the updated task list
                        saveTasksToSharedPreferences();
                    }
                }
            }
    );

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        markDone = view.findViewById(R.id.markDone);
        add_new = view.findViewById(R.id.add_new);
        share = view.findViewById(R.id.share);

        // Set up RecyclerView with the adapter and GridLayoutManager
        cardTaskList = loadTasksFromSharedPreferences();
        if (cardTaskList == null) {
            cardTaskList = generateCardItems();
        }
        cardAdapter = new card_adapter(cardTaskList);

        // Calculate the dynamic span count based on the screen width and desired number of cards in a row
        int screenWidth = getScreenWidth();
        int desiredCardsInRow = 2; // Change this value to the desired number of cards in a row
        int spanCount = screenWidth / (desiredCardsInRow * getCardWidth());

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        recyclerView.setAdapter(cardAdapter);

        // Set the OnTaskCardClickListener to the cardAdapter
        cardAdapter.setOnTaskCardClickListener(this);

        markDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasCheckedTasks()) {
                    showMarkDoneBottomSheet();
                } else {
                    Toast.makeText(getContext(), "No tasks selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the CreateTask activity using the ActivityResultLauncher
                Intent intent = new Intent(getContext(), CreateTask.class);
                createTaskLauncher.launch(intent);
            }
        });

        // Set click listener for the "share" button
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareCheckedItems(); // Call the method to share checked tasks
            }
        });

        return view;
    }

    private List<task_card> loadTasksFromSharedPreferences() {
        // Implementation to load tasks from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("task_list", Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonTasks = sharedPreferences.getString("tasks", null);
        if (jsonTasks != null) {
            Type type = new TypeToken<List<task_card>>() {}.getType();
            return gson.fromJson(jsonTasks, type);
        }
        return null;
    }

    private void saveTasksToSharedPreferences() {
        // Implementation to save tasks to SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("task_list", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonTasks = gson.toJson(cardTaskList);
        editor.putString("tasks", jsonTasks);
        editor.apply();
    }

    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    // This method will give you the width of the card item dynamically
    private int getCardWidth() {
        return (int) (120 * getActivity().getResources().getDisplayMetrics().density);
    }

    private List<task_card> generateCardItems() {
        List<task_card> itemList = new ArrayList<>();
        itemList.add(new task_card("(Sample Task)\nCollege Assignments", "Advanced Java\nClient Side Scripting\nOperating Systems\nEnvironmental Studies", "Date & Time"));
        itemList.add(new task_card("(Sample Task)\nPurchase Groceries", "Biscuits\nCereal\nMilk\nEggs", "Date & Time"));
        itemList.add(new task_card("(Sample Task)\nPay Electricity Bill", "", "Date & Time"));

        // Add more task_card items if needed
        return itemList;
    }

    void removeCheckedItems() {
        Iterator<task_card> iterator = cardTaskList.iterator();
        while (iterator.hasNext()) {
            task_card card = iterator.next();
            if (card.isChecked()) {
                iterator.remove();
            }
        }
        cardAdapter.notifyDataSetChanged();

        // Save the updated task list
        saveTasksToSharedPreferences();
    }

    // Method to share checked tasks
    public void shareCheckedItems() {
        StringBuilder taskDetails = new StringBuilder();
        for (task_card card : cardTaskList) {
            if (card.isChecked()) {
                String title = card.getTitle();
                String description = card.getDescription();
                String dateTime = card.getDateTime();
                taskDetails.append(title).append(" :\n\n ").append(description).append("\n\n\n");
            }
        }
        if (taskDetails.length() > 0) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, taskDetails.toString());
            startActivity(Intent.createChooser(intent, "Share Tasks"));
        } else {
            Toast.makeText(getContext(), "Select task to share", Toast.LENGTH_SHORT).show();
        }
    }

    // Implement the method to handle click events on task cards
    @Override
    public void onTaskCardClick(task_card taskCard, int position) {
        // Handle the click event here
        // Redirect to the CreateTask activity with the data of the selected task_card and its position
        Intent intent = new Intent(getContext(), CreateTask.class);
        intent.putExtra("title", taskCard.getTitle());
        intent.putExtra("description", taskCard.getDescription());
        intent.putExtra("position", position); // Pass the position here
        createTaskLauncher.launch(intent);
    }

    // Method to check if there are any tasks checked
    private boolean hasCheckedTasks() {
        for (task_card card : cardTaskList) {
            if (card.isChecked()) {
                return true;
            }
        }
        return false;
    }

    // Method to show the bottom sliding dialog for marking tasks as done
    private void showMarkDoneBottomSheet() {
        MarkDoneBottomSheetDialogFragment bottomSheetDialogFragment = new MarkDoneBottomSheetDialogFragment();
        bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    private String getCurrentDateTime() {
        // Get the current date and time in the format "dd/MM/yyyy hh:mm:ss a"
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault());
        return sdf.format(new Date());
    }

}