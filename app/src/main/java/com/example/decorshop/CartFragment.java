package com.example.decorshop;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class CartFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private CardView cardView;
    private TextView totalPriceTextView;
    private Button buyNowButton;

    public CartFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = view.findViewById(R.id.cartItems);
        totalPriceTextView = view.findViewById(R.id.totalPriceTextView);
        buyNowButton = view.findViewById(R.id.buyNowButton);
        linearLayout = view.findViewById(R.id.cartListLinearLayout);
        cardView = view.findViewById(R.id.noItemInCart);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set up the recycler view
        List<DecorItem> items = DecorItemCart.getInstance().getDecorItems();
        CartAdapter adapter = new CartAdapter(items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        double totalPrice = 0;
        for (DecorItem item : items) {
            totalPrice =totalPrice + Double.parseDouble(item.getPrice());
        }
        totalPriceTextView.setText("$ "+String.valueOf(totalPrice));
        if(items.size()==0){
            linearLayout.setVisibility(View.GONE);
            cardView.setVisibility(View.VISIBLE);
        }else{
            cardView.setVisibility(View.GONE);
        }
        double finalTotalPrice = totalPrice;
        buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                if(currentUser != null){
                    String uid = currentUser.getUid();
                    Map<String, Object> decorItemsMapList = new HashMap<>();
                    int i =0;
                    for (DecorItem decorItem : DecorItemCart.getInstance().getDecorItems()) {
                        Map<String, Object> decorItemMap = new HashMap<>();
                        decorItemMap.put("name", decorItem.getName());
                        decorItemMap.put("price", decorItem.getPrice());
                        decorItemMap.put("image", decorItem.getImage());
                        decorItemsMapList.put(String.valueOf(i), decorItemMap);
                        i++;
                    }
                    decorItemsMapList.put("Total", finalTotalPrice);
                    decorItemsMapList.put("uid", uid);
                    db.collection("decorItemsCart")
                            .add(decorItemsMapList)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getActivity(), "Order Placed.",
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
                    Toast.makeText(getActivity(), "Login to buy.",
                            Toast.LENGTH_LONG).show();
                }
            }

        });

    }
}