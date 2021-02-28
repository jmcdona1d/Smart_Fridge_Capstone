package com.example.smartfridgeproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Calendar;
import java.util.Date;

public class InsertItem extends AppCompatDialogFragment {
    private EditText foodName;
    private DatePicker insertionDate;
    private DatePicker expiryDate;
    private InsertItemListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.insert_item, null);

        builder.setView(view).setTitle("Insert Item")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //These will be the dates in a format that is recognized by the methods
                Date dateInserted = new Date(insertionDate.getYear(),insertionDate.getMonth(), insertionDate.getDayOfMonth());
                Date dateExpired = new Date(expiryDate.getYear(),expiryDate.getMonth(), expiryDate.getDayOfMonth());
                String food = foodName.getText().toString();
                listener.applyChanges(food, dateInserted, dateExpired);
            }
        });

        foodName = view.findViewById(R.id.inserted_food_name);
        insertionDate = (DatePicker)view.findViewById(R.id.entry_date_chooser);
        expiryDate = (DatePicker)view.findViewById(R.id.expected_expiry);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (InsertItemListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"must implement InsertItemListener");
        }

    }

    public interface InsertItemListener{
        void applyChanges(String food, Date insertionDate, Date expiryDate);
    }
}
