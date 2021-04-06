package com.example.smartfridgeproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.*;
import java.text.SimpleDateFormat;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements InsertItem.InsertItemListener {
    //foodList is the list of food that forms the recyclerview
    private ArrayList<FoodItem> foodList;
    private ArrayList<String> image_urls;
    private static final String API_BASE_URL = "http://76.68.60.198";

    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button buttonInsert;
    private Button buttonContents;

    private boolean canDelete;

    private JSONArray fullResponse;
    private JSONObject apiResults;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.requestQueue = Volley.newRequestQueue(this);
        this.foodList = new ArrayList<>();
        this.image_urls = new ArrayList<>();
        fetchFridgeContents(); //read api/db for current fridge contents to display

        //createFoodList(); hide now that items will get pulled in dynamically

        buttonInsert = findViewById(R.id.button_insert);
        buttonContents = findViewById(R.id.view_contents);

        canDelete=false;

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("", Calendar.getInstance().getTime(), Calendar.getInstance().getTime(), false);
                //InsertItem editTextInsert = new InsertItem();
                //String givenName = editTextInsert.getText().toString();
                //insertItem(givenName, foodList.size());
            }
        });

        buttonContents.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    openContents();
        }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                System.out.println("Deleted");
                removeItem(viewHolder.getAdapterPosition());
                updateDatabase();
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    public void openDialog(String name, Date insertion, Date expiry, boolean deleteOld) {
        InsertItem insertItem = new InsertItem();
        Bundle args = new Bundle();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        args.putString("name", name);
        args.putString("insertion", formatter.format(insertion));
        args.putString("expiry", formatter.format(expiry));
        args.putBoolean("deleteOld", deleteOld);
        insertItem.setArguments(args);

        insertItem.show(getSupportFragmentManager(), "New Food Item");

    }

    public void openContents(){
        ContentView contentView = new ContentView();
        Bundle args = new Bundle();
        args.putString("url1", image_urls.get(0));
        args.putString("url2", image_urls.get(1));
        args.putString("url3", image_urls.get(2));

        contentView.setArguments(args);
        contentView.show(getSupportFragmentManager(), "contents");
    }

    public void insertItem (String name, Date insertionDate, Date expiryDate, int position){
        //SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        //String strInsertionDate = formatter.format(insertionDate);
        //String strExpiryDate = formatter.format(expiryDate);
        foodList.add(position, new FoodItem(0, name, insertionDate, expiryDate, null));
        mAdapter.notifyItemInserted(position);
    }

    public void removeItem(int position){
        foodList.remove(position);
        updateDatabase();
        mAdapter.notifyItemRemoved(position);
    }

    public void changeItem(int position, String text){
        foodList.get(position).changeText1(text);
        mAdapter.notifyItemChanged(position);
    }

    public void createFoodList() {
        //foodList = new ArrayList<>();
        foodList.add(new FoodItem(R.drawable.turnips, "Turnips", new Date(121, 1, 27), new Date(121, 3, 27), null));
        foodList.add(new FoodItem(R.drawable.eggplant, "Eggplant", new Date(121, 1, 17), new Date(121, 3, 7), null));
        foodList.add(new FoodItem(R.drawable.shrimp, "Shrimp", new Date(121, 2, 27), new Date(121, 2, 27), null));
    }
    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(foodList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                //changeItem(position, "Clicked");
                System.out.println("The food name is "+foodList.get(position).getFoodName());
                openDialog(foodList.get(position).getFoodName(), foodList.get(position).getEntryDate(), foodList.get(position).getExpiryDate(), true);
                removeItem(position);
            }
        });
    }

    @Override
    public void applyChanges(String food, Date insertionDate, Date expiryDate) {
        insertItem(food, insertionDate, expiryDate, foodList.size());
        updateDatabase();
    }
    public void updateDatabase(){
        JSONObject encapsulatingItem=new JSONObject();
        JSONObject dataItem = new JSONObject();
        try {
            dataItem.put("total_items", foodList.size());
            JSONArray newItemsJsonList=new JSONArray();
            for (int i=0; i<foodList.size(); i++){
                //Default everything to 0 for now
                JSONObject item = new JSONObject();
                item.put("xmin", 0);
                item.put("xmax", 0);
                item.put("ymin", 0);
                item.put("ymax", 0);
                item.put("class", 0);
                item.put("class_text", foodList.get(i).getFoodName());
                item.put("softmax", 0);
                JSONObject timestamp = new JSONObject();
                timestamp.put("$date", foodList.get(i).getEntryDate());
                item.put("timestamp_added", timestamp);
                item.put("image_url", "Filler");
                newItemsJsonList.put(item);
            }
            dataItem.put("items", newItemsJsonList);
            JSONArray images_JSON = new JSONArray(this.image_urls);
            dataItem.put("fridge_images", images_JSON);
            encapsulatingItem = (JSONObject) fullResponse.get(0);
            encapsulatingItem.put("data", dataItem);
            System.out.println("The encapsulating item is "+encapsulatingItem);
            //fullResponse.put(encapsulatingItem);
        }
        catch (Throwable t){
            System.out.println("Error in JSON"+t);
        }
        System.out.println("Right before the request, the fullResponse is "+fullResponse);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, API_BASE_URL+":40002/appUpdate/"+dataItem, dataItem, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               System.out.println(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error in upload: "+error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-OAPI-Key","TQEEGSk8OgWlhteL8S8siKao2q6LIGdq");
                headers.put("X-ISS-Key","2b2dd0d9dbb54ef79b7ee978532bc823");
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    public void fetchFridgeContents(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_BASE_URL+":40002/contents",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            fullResponse = new JSONArray(response);
                            //System.out.println("for experimental purposes, we find "+fullResponse.get(0));
                            String[] data = response.split(" \"data\"");

                            String json_String = data[1].substring(1, data[1].length() - 2);


                            System.out.println(json_String);
                            apiResults = new JSONObject(json_String);
                            //System.out.println(apiResults.keys());

                            JSONArray items = (JSONArray) apiResults.get("items");

                            for(int i = 0; i < items.length(); i++){
                                JSONObject item = (JSONObject) items.get(i);
                                JSONObject dateObj = (JSONObject) item.get("timestamp_added");
                                Date foodAdded = new Date();
                                foodAdded.setTime((long) dateObj.get("$date")); //need to create expiry date as well to be this date + x days
                                //foodList.add(new FoodItem(0, (String) item.get("class_text"), foodAdded, foodAdded, (String) item.get("image_url")));
                                foodList.add(new FoodItem(0, (String) item.get("class_text"), foodAdded, foodAdded, (String) item.get("image_url")));
                            }

                            JSONArray urls = (JSONArray) apiResults.get("fridge_images");
                            for (int i = 0; i <urls.length(); i++){
                                image_urls.add((String) urls.get(i));
                            }

                            //System.out.println("The foodlist is "+ foodList);
                            buildRecyclerView();

                        }
                        catch (Throwable t){
                            System.out.println("Error in JSON"+t);
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        System.out.println(error.toString());
                    }
                });
        this.requestQueue.add(stringRequest);
    }
}