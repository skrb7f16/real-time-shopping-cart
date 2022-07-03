package com.skrb7f16.canteen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.skrb7f16.canteen.BillGenerateActivity;
import com.skrb7f16.canteen.R;
import com.skrb7f16.canteen.models.Item;

import java.util.ArrayList;
import java.util.List;

public class BillAdpater extends RecyclerView.Adapter<BillAdpater.BillViewHolder> {
    List<Item> items;
    String code;
    FirebaseAuth auth;
    FirebaseDatabase database;
    public BillAdpater(List<Item> items, Context context, String code) {
        this.items = items;
        this.code = code;
    }

    public BillAdpater() {

    }


    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item,parent,false);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance("https://canteen-65354-default-rtdb.firebaseio.com/");
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        final Item item=items.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText(""+item.getQuantity());
        holder.price.setText(String.valueOf(item.getPrice()));
        holder.total.setText(String.valueOf(item.getQuantity()*item.getPrice()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class BillViewHolder extends RecyclerView.ViewHolder {
        TextView name,price,quantity,total;
        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.bill_item_name);
            price=itemView.findViewById(R.id.bill_item_price);
            quantity=itemView.findViewById(R.id.bill_item_quantity);
            total=itemView.findViewById(R.id.bill_item_total);
        }
    }
}
