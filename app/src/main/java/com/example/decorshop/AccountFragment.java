package com.example.decorshop;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class AccountFragment extends Fragment {
    private TextView signin;
    private CardView card1;
    private TextView signout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        LinearLayout parentLayout = view.findViewById(R.id.orders_layout);
        LayoutInflater orderInflater = LayoutInflater.from(getContext());
        signin= view.findViewById(R.id.signin);
        card1= view.findViewById(R.id.card1);
        signout= view.findViewById(R.id.signout);
        if(currentUser != null){
            signout.setVisibility(View.VISIBLE);
            card1.setVisibility(View.VISIBLE);
            signin.setVisibility(View.GONE);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("decorItemsCart")
                    .whereEqualTo("uid", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    View cardView = orderInflater.inflate(R.layout.order_card, parentLayout, false);
                                    TextView itemsName = cardView.findViewById(R.id.itemsName);
                                    TextView price = cardView.findViewById(R.id.totalPrice);
                                    price.setText(" ");
                                    itemsName.setText("No orders placed.");
                                    parentLayout.addView(cardView);

                                }else{
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        View cardView = orderInflater.inflate(R.layout.order_card, parentLayout, false);
                                        TextView itemsName = cardView.findViewById(R.id.itemsName);
                                        TextView price = cardView.findViewById(R.id.totalPrice);
                                        Map<String, Object> dataMap = document.getData();
                                        String names="";
                                        if (dataMap != null) {
                                            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                                                String key = entry.getKey();
                                                Object value = entry.getValue();
                                                if(key.equals("Total")){
                                                    price.setText("Totoal: $"+entry.getValue().toString());
                                                }
                                                if (value instanceof Map) {
                                                    Map<String, Object> innerMap = (Map<String, Object>) value;
                                                    String innerValue = (String) innerMap.get("name");
                                                    names = names +innerValue +"\n";
                                                }
                                            }
                                            itemsName.setText(names);
                                            parentLayout.addView(cardView);
                                        }
                                    }
                                }
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }else{
            signin.setVisibility(View.VISIBLE);
            card1.setVisibility(View.GONE);
            signout.setVisibility(View.GONE);
        }
        // Login
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SigninActivity.class);
                startActivity(intent);
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Toast.makeText(getActivity(), "Signed Out.",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}