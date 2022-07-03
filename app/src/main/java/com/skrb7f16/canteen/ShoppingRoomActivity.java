package com.skrb7f16.canteen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skrb7f16.canteen.adapters.ItemsAdapter;
import com.skrb7f16.canteen.databinding.ActivityBillGenerateBinding;
import com.skrb7f16.canteen.databinding.ActivityShoppingRoomBinding;
import com.skrb7f16.canteen.models.Item;
import com.skrb7f16.canteen.models.ShoppingRoom;
import com.skrb7f16.canteen.models.User;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class ShoppingRoomActivity extends AppCompatActivity {

    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseUser user;
    ActivityShoppingRoomBinding binding;
    String code;
    User userModel;
    ShoppingRoom shoppingRoom;
    Item item;
    ItemsAdapter itemsAdapter;
    boolean gotShoppingRoom =false;
    List<Item> list= new ArrayList<Item>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_room);
        binding=ActivityShoppingRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        code=intent.getStringExtra("code");
        binding.shoppingRoomId.setText("Add item to "+code);
        itemsAdapter=new ItemsAdapter(list,this,code);
        binding.itemsRecyclerView.setAdapter(itemsAdapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.itemsRecyclerView.setLayoutManager(layoutManager);

        Log.d("meow", "onCreate: "+code);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance("https://canteen-65354-default-rtdb.firebaseio.com/");
        user=auth.getCurrentUser();
        userModel=new User(user.getDisplayName(),user.getUid());
        getRoom();

        database.getReference().child("Rooms").child(code).child("items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot d:snapshot.getChildren()){
                    Item item=d.getValue(Item.class);
                    list.add(item);
                }
                itemsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.itemName.getText().toString().length()!=0){
                    addItem();
                }
                else{
                    Toast.makeText(ShoppingRoomActivity.this,"Cannot be empty name",Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.generateBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(ShoppingRoomActivity.this, BillGenerateActivity.class);
                intent1.putExtra("code",code);
                startActivity(intent1);
            }
        });


    }

    public void getRoom(){
        database.getReference().child("Rooms").child(code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                shoppingRoom=snapshot.getValue(ShoppingRoom.class);

                if(gotShoppingRoom==false){
                    if(shoppingRoom.getCode().equals(code)) {

                        if (shoppingRoom.memberExists(userModel)==false) {
                            shoppingRoom.addMembers(userModel);
                            updateRoom();
                        }
                        gotShoppingRoom=true;
                    }

                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("meow", "onCancelled: ");
            }
        });
    }

    public void addItem(){
        for(int i=0;i<shoppingRoom.getItems().size();i++){
            if(shoppingRoom.getItems().get(i).getName().equals(binding.itemName.getText().toString())){
                shoppingRoom.getItems().get(i).increase();
                updateRoom();
                binding.itemName.setText("");
                binding.itemPrice.setText("");
                return;
            }
        }
        item=new Item(binding.itemName.getText().toString(),1,Double.parseDouble(binding.itemPrice.getText().toString()));
        shoppingRoom.addItem(item);
        updateRoom();
        binding.itemName.setText("");
        binding.itemPrice.setText("");
    }

    public void updateRoom(){
        database.getReference().child("Rooms").child(code).setValue(shoppingRoom);

    }


}