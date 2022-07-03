package com.skrb7f16.canteen.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.skrb7f16.canteen.R;
import com.skrb7f16.canteen.models.Item;
import com.skrb7f16.canteen.models.ShoppingRoom;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    List<Item> items;
    Context context;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String code;
    public ItemsAdapter(List<Item> items,Context context,String code){
        this.context=context;
        this.items=items;
        this.code=code;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_item,parent,false);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance("https://canteen-65354-default-rtdb.firebaseio.com/");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Item item=items.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText(""+item.getQuantity());
        holder.price.setText(String.valueOf(item.getPrice()));
        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.increase();

                //holder.quantity.setText(""+item.getQuantity());
                database.getReference().child("Rooms").child(code).child("items").child(""+position).setValue(item);
            }
        });

        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getQuantity()==0)return;
                item.decrease();
                if(item.getQuantity()==0){
                    database.getReference().child("Rooms").child(code).child("items").child(""+position).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            return;
                        }
                    });
                }

                //holder.quantity.setText(""+item.getQuantity());
                else database.getReference().child("Rooms").child(code).child("items").child(""+position).setValue(item);

            }
        });
    }

    @Override
    public int getItemCount() {
        if(items!=null)return items.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView increase,decrease;
        TextView name,quantity,price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            increase=itemView.findViewById(R.id.increase);
            decrease=itemView.findViewById(R.id.remove);
            name=itemView.findViewById(R.id.name);
            quantity=itemView.findViewById(R.id.quantity);
            price=itemView.findViewById(R.id.price);
        }
    }
}
