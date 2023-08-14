package com.example.decorshop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {
    private RecyclerView recyclerView;
    private DecorItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        recyclerView = view.findViewById(R.id.decor_item_list);

        recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(),1));
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("decorItems").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DecorItem> decorItemList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.i("tag",document.toString());
                    DecorItem DecorItem = new DecorItem(document);
                    decorItemList.add(DecorItem);
                }
                adapter = new DecorItemAdapter(decorItemList,getContext());
                recyclerView.setAdapter(adapter);
            }
        });
    }
}
