package com.skrb7f16.canteen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skrb7f16.canteen.databinding.ActivityJoinRoomBinding;
import com.skrb7f16.canteen.models.ShoppingRoom;

public class JoinRoomActivity extends AppCompatActivity {

    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseUser user;
    ActivityJoinRoomBinding binding;
    String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);
        binding=ActivityJoinRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance("https://canteen-65354-default-rtdb.firebaseio.com/");
        auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        binding.goToRoomFromJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code=binding.code.getText().toString();
                checkForRoom();
            }
        });
    }

    public void checkForRoom(){

        database.getReference().child("Rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(code)){
                    Intent intent=new Intent(JoinRoomActivity.this, ShoppingRoomActivity.class);
                    intent.putExtra("code",code);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(JoinRoomActivity.this,"Cannot find this room",Toast.LENGTH_SHORT).show();
                    binding.code.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}