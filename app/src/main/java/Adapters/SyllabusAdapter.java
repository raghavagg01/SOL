package Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.application.kurukshetrauniversitypapers.LoginActivity2;
import com.application.kurukshetrauniversitypapers.R;
import utils.SingleDownloadClass;
import utils.uploadPDF;
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

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class SyllabusAdapter extends ArrayAdapter<uploadPDF> implements ActivityCompat.OnRequestPermissionsResultCallback  {
    private Activity context;
    List<uploadPDF> pdflist;
    FirebaseAuth mAuth;
    StorageReference storageReference,myref;
    FirebaseStorage firebaseStorage;
    DatabaseReference rootref;
    private String branch;
    private String semester;
    private final int STORAGE_PERMISSION_CODE = 1;

    public SyllabusAdapter(Activity context, List<uploadPDF> pdflist) {
        super(context, R.layout.pdflist_row, pdflist);
        this.context = context;
        this.pdflist = pdflist;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.pdflist_row, null, true);

        final TextView textViewName = (TextView) listViewItem.findViewById(R.id.pdfname);
        Button syllabus_download = (Button) listViewItem.findViewById(R.id.download_single);
        Button btnshare =(Button) listViewItem.findViewById(R.id.share);
        uploadPDF uploadPDF = pdflist.get(position);
        textViewName.setText(uploadPDF.getName());
        Log.e("",uploadPDF.getName());
        mAuth=FirebaseAuth.getInstance();

        textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFiles(position);
            }
        });
        btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((mAuth.getCurrentUser()!=null && mAuth.getCurrentUser().isEmailVerified())){
                    Uri uri = getLink(position);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareBody = uri.toString();
                    String shareSubject = "Paper Link";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                    context.startActivity(Intent.createChooser(shareIntent, "Share Using"));
                }
                else {
                    checkAuthentication();
                }

            }

            private void checkAuthentication() {
                String[] items = {"Yes", "No"};
                AlertDialog.Builder dialog= new AlertDialog.Builder(getContext());
                dialog.setTitle("To Share you need to Login");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            Intent intent=new Intent(getContext(), LoginActivity2.class);
                            context.startActivity(intent);
                        }
                        if(which ==1){
                            // do nothing
                        }
                    }
                });
                dialog.create().show();
            }
        });
        syllabus_download.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View view) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    SingleDownloadClass singleDownloadClass = new SingleDownloadClass();
                    branch = singleDownloadClass.getBranch();
                    semester = singleDownloadClass.getSemester();
                    Log.e("dir", "IN/KU" + "/" + branch + "/" + semester + "/" + "Syllabus");
                    download("IN/KU" + "/" + branch + "/" + semester + "/" + "Syllabus", textViewName.getText().toString());
                }
                else {
                    requestStoragePermission();
                }
            }
        });
        return listViewItem;
    }

    public Uri getLink(int position){
        uploadPDF uploadPDF = pdflist.get(position);
        return ( Uri.parse(uploadPDF.getUrl()) );
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
        toast();
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
                        downloadfiles(getContext(),url);
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

    public void downloadfiles(Context context, String url)
    {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String title = URLUtil.guessFileName(url,null,null);
        request.setTitle(title);
        String cookie = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie",cookie);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title);
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(context)
                    .setTitle("Permission needed")
                    .setMessage("Kindly click OK and then allow Qsol to access your storage, such that required files can be downloaded.")
                    .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(context, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE))
                    .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(context, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

}


