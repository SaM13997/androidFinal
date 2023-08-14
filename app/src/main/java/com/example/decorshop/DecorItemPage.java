package com.example.decorshop;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DecorItemPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoritempage);
        // Get the item information passed from MainActivity
        Intent intent = getIntent();
        String decorItemName = intent.getStringExtra("name");
        String imageURL = intent.getStringExtra("image");
        String price = intent.getStringExtra("price");
        ImageView decorItemImage = findViewById(R.id.decorItemImage);
        TextView txtShoeName = findViewById(R.id.decorItemName);
        TextView decorItemPrice = findViewById(R.id.decorItemPrice);
        Button addToCartButton = findViewById(R.id.btnAddToCart);
        Button buyNowButton = findViewById(R.id.btnBuyNow);
        txtShoeName.setText(decorItemName);
        decorItemPrice.setText("Price: $"+price);
        Glide.with(this)
                .load(imageURL)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_foreground))
                .into(decorItemImage);
        addToCartButton.setOnClickListener(view -> {
            DecorItem decorItem = new DecorItem(decorItemName,price,imageURL);
            DecorItemCart.getInstance().addDecorItem(decorItem);
            Toast.makeText(DecorItemPage.this, "Added to cart.",
                    Toast.LENGTH_SHORT).show();
        });
        buyNowButton.setOnClickListener(view -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            DecorItem decorItem = new DecorItem(decorItemName,price,imageURL);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if(currentUser != null){
                String uid = currentUser.getUid();
                Map<String, Object> decorItemsMapList = new HashMap<>();
                Map<String, Object> decorItemMap = new HashMap<>();
                decorItemMap.put("name", decorItem.getName());
                decorItemMap.put("price", decorItem.getPrice());
                decorItemMap.put("image", decorItem.getImage());
                decorItemsMapList.put("0", decorItemMap);
                decorItemsMapList.put("Total", price);
                decorItemsMapList.put("uid", uid);
                db.collection("decorItemsCart")
                        .add(decorItemsMapList)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(DecorItemPage.this, "Order Placed.",
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle upload failure
                                Log.e("TAG", "Error uploading decorItem: " + e.getMessage());
                            }
                        });
            }else {
                Toast.makeText(DecorItemPage.this, "Login to buy.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
