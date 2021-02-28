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

import java.text.SimpleDateFormat;
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

        foodName = view.findViewById(R.id.inserted_food_name);
        insertionDate = (DatePicker)view.findViewById(R.id.entry_date_chooser);
        expiryDate = (DatePicker)view.findViewById(R.id.expected_expiry);


        init();

        builder.setView(view).setTitle("Insert Item")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (init())
                    sendData();
            }
        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendData();
            }
        });
        Dialog current=builder.create();
        current.setCanceledOnTouchOutside(false);
        return current;
    }

    private boolean init(){
        String defaultName;
        Date defaultInserted;
        Date defaultExpired;
        boolean deleteOld=false;

        try {
            defaultName=getArguments().getString("name");
            String defaultInsertedString=getArguments().getString("insertion");
            String defaultExpiredString=getArguments().getString("expiry");
            deleteOld=getArguments().getBoolean("deleteOld");
            defaultInserted=new SimpleDateFormat("MM/dd/yyyy").parse(defaultInsertedString);
            defaultExpired=new SimpleDateFormat("MM/dd/yyyy").parse(defaultExpiredString);

            foodName.setText(defaultName.trim());
            insertionDate.updateDate(defaultInserted.getYear()+1900, defaultInserted.getMonth(), defaultInserted.getDate());
            expiryDate.updateDate(defaultExpired.getYear()+1900, defaultExpired.getMonth(), defaultExpired.getDate());
        }
        catch (Exception e){
            System.out.println(e);
        }
        return deleteOld;
    }

    private void sendData(){
        Date dateInserted = new Date(insertionDate.getYear()-1900,insertionDate.getMonth(), insertionDate.getDayOfMonth());
        Date dateExpired = new Date(expiryDate.getYear()-1900,expiryDate.getMonth(), expiryDate.getDayOfMonth());
        String food = foodName.getText().toString().trim();
        listener.applyChanges(food, dateInserted, dateExpired);
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
