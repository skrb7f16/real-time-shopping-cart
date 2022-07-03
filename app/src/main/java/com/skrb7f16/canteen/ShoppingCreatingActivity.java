package com.skrb7f16.canteen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.skrb7f16.canteen.databinding.ActivityShoppingCreatingBinding;
import com.skrb7f16.canteen.models.ShoppingRoom;
import com.skrb7f16.canteen.models.User;

import java.util.Random;

public class ShoppingCreatingActivity extends AppCompatActivity {

    FirebaseDatabase database;
    ActivityShoppingCreatingBinding binding;
    FirebaseAuth auth;
    int code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_creating);
        binding=ActivityShoppingCreatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Random random=new Random();
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance("https://canteen-65354-default-rtdb.firebaseio.com/");
        if(auth==null||database==null){
            finish();
        }
        FirebaseUser user=auth.getCurrentUser();
        binding.usernameDisplay.setText("Created by " +user.getDisplayName().toString());
        User userCreator=new User(user.getDisplayName(),user.getUid());
        code =random.nextInt(10000)-random.nextInt(1000);
        if(code<0)code*=-1;
        final ShoppingRoom shoppingRoom=new ShoppingRoom();
        shoppingRoom.setCode(String.valueOf(code));
        shoppingRoom.setCreater(userCreator);
        shoppingRoom.addMembers(userCreator);
        binding.codeDisplay.setText(String.valueOf(code));
        database.getReference().child("Rooms").child(String.valueOf(code)).setValue(shoppingRoom);
        binding.goToRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ShoppingCreatingActivity.this, ShoppingRoomActivity.class);
                intent.putExtra("code",""+code);
                startActivity(intent);
            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"I invite you to join my online shopping cart app enter the code \n"+code);
                startActivity(intent);
            }
        });
    }
}