package com.skrb7f16.canteen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skrb7f16.canteen.adapters.BillAdpater;
import com.skrb7f16.canteen.adapters.ItemsAdapter;
import com.skrb7f16.canteen.databinding.ActivityBillGenerateBinding;
import com.skrb7f16.canteen.databinding.ActivityJoinRoomBinding;
import com.skrb7f16.canteen.models.Item;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BillGenerateActivity extends AppCompatActivity {
    String code=""+2691;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseUser user;
    ActivityBillGenerateBinding binding;
    List<Item> items=new ArrayList<>();
    BillAdpater billAdpater;
    FirebaseStorage storage;
    String fileName="";
    Uri downloadUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_generate);
        binding=ActivityBillGenerateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        code=getIntent().getStringExtra("code");
        database=FirebaseDatabase.getInstance("https://canteen-65354-default-rtdb.firebaseio.com/");
        auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        storage=FirebaseStorage.getInstance("gs://canteen-65354.appspot.com");

        billAdpater=new BillAdpater(items,this,code);
        binding.billRecyler.setAdapter(billAdpater);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.billRecyler.setLayoutManager(layoutManager);
        database.getReference().child("Rooms").child(code).child("items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                double total=0;
                for(DataSnapshot d:snapshot.getChildren()){
                    Item temp=d.getValue(Item.class);
                    total+=(temp.getPrice()*temp.getQuantity());
                    items.add(temp);
                }
                Item i=new Item();
                i.setName("Total");
                i.setPrice(total);
                i.setQuantity(1);
                items.add(i);
                billAdpater.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.Generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertToPdf();
            }
        });
        binding.Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog p=new ProgressDialog(BillGenerateActivity.this);
                p.setMessage("Please wait");
                p.show();
                p.setCancelable(false);
                Uri file = Uri.fromFile(new File(fileName));
                Log.d("hello", "onSuccess: "+fileName);
                String uid = UUID.randomUUID().toString();

                final StorageReference ref = storage.getReference().child(uid+file.getLastPathSegment());
                Task uploadtask=ref.putFile(file);
                Task<Uri> urlTask=uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful())throw task.getException();
                        Log.d("hello", "onComplete: ");
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            p.hide();
                            Log.d("hello", "onComplete: "+task.getResult().toString());
                            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://api.whatsapp.com/send?phone=" + binding.phoneNumber.getText().toString() + "&text=" + task.getResult().toString())));
                        }
                    }
                });
            }
        });
    }

    void convertToPdf(){
        LinearLayout lt=binding.bill;
        Log.d("hello", "onSuccess:1 ");
        PdfGenerator.getBuilder().setContext(this).fromViewSource().fromView(binding.bill)
                .setFileName(code)
                .setFolderNameOrPath("invoices")
                .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
                .build(new PdfGeneratorListener() {
                    @Override
                    public void onStartPDFGeneration() {
                        Log.d("hello", "onSuccess: 3");
                    }

                    @Override
                    public void onFinishPDFGeneration() {
                        Log.d("hello", "onSuccess: 2");
                    }

                    @Override
                    public void showLog(String log) {
                        super.showLog(log);
                    }

                    @Override
                    public void onSuccess(SuccessResponse response) {
                        super.onSuccess(response);
                        fileName=response.getPath();
                        Log.d("hello", "onSuccess: ");
                    }

                    @Override
                    public void onFailure(FailureResponse failureResponse) {
                        super.onFailure(failureResponse);
                    }
                });
    }
}