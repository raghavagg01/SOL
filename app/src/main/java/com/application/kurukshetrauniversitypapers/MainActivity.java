package com.application.kurukshetrauniversitypapers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button btechbtn, quick_search, mbabtn, bcabtn, managementbtn, kubtn;
    FirebaseAuth mAuth;
    private DrawerLayout drawer;
    TextView total_papers;
    private FirebaseAnalytics mFirebaseAnalytics;
    public static boolean firstStart;
    public static int count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        firstStart = prefs.getBoolean("firstStart", true);

        setContentView(R.layout.activity_main);
        btechbtn = findViewById(R.id.btechbtn);
        quick_search = findViewById(R.id.quick_search);
        mbabtn = findViewById(R.id.mbabtn);
        bcabtn = findViewById(R.id.bcabtn);
        managementbtn = findViewById(R.id.managementbtn);
        kubtn = findViewById(R.id.kubtn);
        total_papers = findViewById(R.id.total_papers);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        checkConnection();
        startCountAnimation();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        updateNavHeader();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("MyNotifications", "MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Successful";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });



        quick_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewFilterSearch.class));
            }
        });

        managementbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Management_expendable_list.class));
            }
        });
        btechbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Btech_expendable_list.class));
            }
        });
    }

    public void updateNavHeader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.username);
        TextView navWelcome = headerView.findViewById(R.id.txt_welcome);
        if (mAuth.getCurrentUser() != null) {
            navWelcome.setVisibility(View.VISIBLE);
            navUsername.setText(mAuth.getCurrentUser().getEmail());
        } else {
            navWelcome.setVisibility(View.INVISIBLE);
            navUsername.setText("Sign in to Share Papers");
        }
    }
    public void progress(View view) {
        Toast.makeText(this, "Will be uploaded soon", Toast.LENGTH_SHORT).show();
    }
    public void website(View view) {
        Intent Browserintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.kuk.ac.in/"));
        startActivity(Browserintent);
    }
    public void checkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activenetwork = manager.getActiveNetworkInfo();
        if (null != activenetwork) {

            if (activenetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                //Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Turn on internet connection", Toast.LENGTH_SHORT).show();

        }
    }
    public void startCountAnimation() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 861);
        animator.setDuration(2500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                total_papers.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_signin:
                if (mAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    break;
                } else {
                    Toast.makeText(this, "You are already logged in", Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.action_logout:
                if ((mAuth.getCurrentUser() == null)) {
                    Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    String[] items = {"Yes", "No"};
                    androidx.appcompat.app.AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("Are you sure to log out?");
                    dialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                mAuth.getInstance().signOut();
                                Toast.makeText(MainActivity.this, "You have been logged out", Toast.LENGTH_SHORT).show();
                            }
                            if (which == 1) {

                            }
                        }
                    });
                    dialog.create().show();
                    break;
                }
            case R.id.source_code:
                Intent Browserintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/raghavagg01/Qsol"));
                startActivity(Browserintent);
                break;
            case R.id.rate:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + getPackageName())));
                    break;
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                    break;
                }

            case R.id.write_feedback:
                startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
                break;


        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (firstStart) {
                startActivity(new Intent(MainActivity.this, ActivityStarRating.class));
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstStart", false);
                editor.apply();
            } else
                System.exit(0);
        }
        super.onBackPressed();
    }

}
