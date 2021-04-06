package com.example.smartfridgeproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ContentView extends AppCompatDialogFragment {
    private String image_1;
    private String image_2;
    private String image_3;
    private ImageView image_display1;
    private ImageView image_display2;
    private ImageView image_display3;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.content_dialog, null);

        image_display1 = view.findViewById(R.id.fridge_image_1);
        image_display2 = view.findViewById(R.id.fridge_image_2);
        image_display3 = view.findViewById(R.id.fridge_image_3);

        try{
            this.image_1 = getArguments().getString("url1");
            this.image_2 = getArguments().getString("url2");
            this.image_3 = getArguments().getString("url3");
        }
        catch(Exception e){
            System.out.println(e);
        }


        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    InputStream is1 = (InputStream) new URL(image_1).getContent();
                    Drawable d1 = Drawable.createFromStream(is1, "name");
                    image_display1.setImageDrawable(d1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is2 = (InputStream) new URL(image_2).getContent();
                    Drawable d2 = Drawable.createFromStream(is2, "name2");
                    image_display2.setImageDrawable(d2);
                } catch(Exception e){
                    e.printStackTrace();
                }

            }
        });

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is3 = (InputStream) new URL(image_3).getContent();
                    Drawable d3 = Drawable.createFromStream(is3, "name3");
                    image_display3.setImageDrawable(d3);
                } catch(Exception e){
                    e.printStackTrace();
                }

            }
        });

        thread1.start();
        thread2.start();
        thread3.start();

        builder.setView(view).setTitle("Fridge Contents:")
                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        Dialog current=builder.create();
        current.setCanceledOnTouchOutside(false);
        return current;
    }
}


