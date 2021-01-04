package com.example.smartfridgeproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //foodList is the list of food that forms the recyclerview
    private ArrayList<FoodItem> foodList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button buttonInsert;
    private Button buttonRemove;
    private EditText editTextInsert;
    private EditText editTextRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createFoodList();
        buildRecyclerView();

        buttonInsert = findViewById(R.id.button_insert);
        buttonRemove = findViewById(R.id.button_remove);
        editTextInsert = findViewById(R.id.edittext_insert);
        editTextRemove = findViewById(R.id.edittext_remove);

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(editTextInsert.getText().toString());
                insertItem(position);
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(editTextRemove.getText().toString());
                removeItem(position);
            }
        });
    }

    //The way this function is set up is temporary
    public void insertItem (int position){
        //Here, we need to populate it with the correct fields
        foodList.add(position, new FoodItem(R.drawable.eggplant, "Eggplant", "2021/01/01", "2021/02/15"));
        mAdapter.notifyItemInserted(position);
    }

    public void removeItem(int position){
        foodList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void createFoodList() {
        foodList = new ArrayList<>();
        foodList.add(new FoodItem(R.drawable.turnips, "Turnips", "10/31/2020", "11/15/2020"));
        foodList.add(new FoodItem(R.drawable.eggplant, "Eggplant", "10/20/2020", "11/01/2020"));
        foodList.add(new FoodItem(R.drawable.shrimp, "Shrimp", "10/15/2020", "10/24/2020"));
    }
    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(foodList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
}