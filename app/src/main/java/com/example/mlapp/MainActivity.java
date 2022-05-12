package com.example.mlapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final int IMAGE_REQUEST_CODE = 333;
    final int TEXT_REQUEST_CODE = 666;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    protected void onStart() {

        super.onStart();

        Button pickImageButton = findViewById(R.id.pickImageButton);
        Button pickTextButton = findViewById(R.id.pickTextButton);

        pickImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                pickImage();
            }
        });

        pickTextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                pickText();
            }
        });
    }

    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    private void pickText(){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, TEXT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            ImageView imageView = findViewById(R.id.imageView);
            Bitmap bitmap = getImageFromData(data);
            imageView.setImageBitmap(bitmap);

            switch (requestCode){

                case IMAGE_REQUEST_CODE:

                    System.out.println("IMAGE");
                    processImageTagging(bitmap);
                    break;

                case TEXT_REQUEST_CODE:

                    System.out.println("TEXT");
                    processTextTagging(bitmap);
                    break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap getImageFromData(Intent data) {
        Uri selectedImage = data.getData();
        Bitmap result = null;
        try {
            
            result = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void processImageTagging(Bitmap bitmap) {
        FirebaseVisionImage visionImg = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                .getOnDeviceImageLabeler();

        labeler.processImage(visionImg)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> tags) {
                        TextView labelTextView = findViewById(R.id.labelTextView);

                        String tagList = "";

                        for (FirebaseVisionImageLabel tag : tags) {

                            tagList += tag.getText();
                            tagList += " ";
                        }

                        labelTextView.setText(tagList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.wtf("ERROR", e);
                    }
                });
    }

    private void processTextTagging(Bitmap bitmap) {

        FirebaseVisionImage visionImg = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        detector.processImage(visionImg)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText text) {
                        TextView labelTextView = findViewById(R.id.labelTextView);

                        labelTextView.setText("text");
                        labelTextView.setText(text.getText());
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.wtf("ERROR", e);
                            }
                        });

    }
}
