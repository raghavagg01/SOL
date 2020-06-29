package com.application.kurukshetrauniversitypapers;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class Pdflistadapter extends ArrayAdapter<uploadPDF> {
    private Activity context;
    List<uploadPDF> pdflist;
    FirebaseAuth mAuth;
    StorageReference storageReference,myref;
    FirebaseStorage firebaseStorage;
    DatabaseReference rootref;
    private String branch;
    private String semester;
    private String code;


    public Pdflistadapter(Activity context, List<uploadPDF> pdflist) {
        super(context, R.layout.pdflist_row, pdflist);
        this.context = context;
        this.pdflist = pdflist;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.pdflist_row, null, true);

        final TextView textViewName = (TextView) listViewItem.findViewById(R.id.pdfname);
        Button btndownload = (Button) listViewItem.findViewById(R.id.download_single);

        uploadPDF uploadPDF = pdflist.get(position);
        textViewName.setText(uploadPDF.getName());
        mAuth=FirebaseAuth.getInstance();

        textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFiles(position);

            }
        });
        btndownload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
           SingleDownloadClass singleDownloadClass=new SingleDownloadClass();
            branch=singleDownloadClass.getBranch();
             semester=singleDownloadClass.getSemester();
              code=singleDownloadClass.getCode();
                  toast();
                download("IN/KU"+"/"+branch+"/"+semester+"/"+code, textViewName.getText().toString());
                Log.e("dir","IN/KU"+"/"+branch+"/"+semester+"/"+code);
//                if (mAuth.getCurrentUser() != null) {
//                    //Toast.makeText(context, "The user is already logged in", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(context, "solution", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                String[] items = {"Yes", "No"};
//                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//                dialog.setTitle("To get the solution login first");
//                dialog.setItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == 0) {
//                            Intent intent = new Intent(context, LoginActivity.class);
//                            context.startActivity(intent);
//                        }
//                        if (which == 1) {
//
//                        }
//                    }
//                });
//                dialog.create().show();
//            }
            }
        });
        return listViewItem;
    }

    public void viewFiles(int position){
        uploadPDF uploadPDF = pdflist.get(position);
        Intent intent = new Intent();
        intent.setType(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(uploadPDF.getUrl());
        if (uri.toString().contains(".pdf")) {
            intent.setDataAndType(uri, "application/pdf");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    public void download(final String directory, final String filename) {


        storageReference = firebaseStorage.getInstance().getReference(directory);
        rootref= FirebaseDatabase.getInstance().getReference(directory);

        rootref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myref = storageReference.child(filename+".pdf");
                myref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        downloadfiles(getContext(),filename, ".pdf", DIRECTORY_DOWNLOADS, url);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void toast(){Toast.makeText(context, "downloading", Toast.LENGTH_LONG).show();}


    public void downloadfiles(Context context, String file, String fileExtension, String destinationDirectory, String url)
    {
        DownloadManager downloadmanager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request =new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, file+fileExtension);
        downloadmanager.enqueue(request);

    }

}


