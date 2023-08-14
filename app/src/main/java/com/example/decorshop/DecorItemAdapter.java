package com.example.decorshop;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class DecorItemAdapter extends RecyclerView.Adapter<DecorItemAdapter.ViewHolder> {
    public List<DecorItem> decorItemList;
    private Context context;
    public DecorItemAdapter(List<DecorItem> decorItemList, Context context) {
        this.decorItemList = decorItemList;
        this.context=context;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView priceTextView;
        public ImageView productImage;
        public Button addToCartButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.DecorItem_Name);
            priceTextView = itemView.findViewById(R.id.DecorItem_Price);
            productImage = itemView.findViewById(R.id.DecorItem_ImageView);
            addToCartButton = itemView.findViewById(R.id.addtocart_button);
            addToCartButton.setOnClickListener(view -> {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                int position = getAdapterPosition();
                DecorItem decorItem = decorItemList.get(position);
                DecorItemCart.getInstance().addDecorItem(decorItem);
                Toast.makeText(context, "Added to cart.",
                        Toast.LENGTH_SHORT).show();
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    DecorItem decorItem = decorItemList.get(position);
                    Intent intent = new Intent(view.getContext(), DecorItemPage.class);
                    intent.putExtra("name", decorItem.getName());
                    intent.putExtra("image", decorItem.getImage());
                    intent.putExtra("price", decorItem.getPrice());
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.decor_item_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DecorItem decorItem = decorItemList.get(position);
        holder.nameTextView.setText(decorItem.getName());
        holder.priceTextView.setText(String.format("$"+ decorItem.getPrice()));
        Glide.with(context)
                .load(decorItem.getImage())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_foreground))
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {

        return decorItemList.size();
    }
}
