package com.application.kurukshetrauniversitypapers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button btechbtn,quicksearch, computer_applications_btn, managementbtn, kubtn,actionbar_signup,diploma_btn;
    FirebaseAuth mAuth;
    private DrawerLayout drawer;
    TextView total_papers;
    private FirebaseAnalytics mFirebaseAnalytics;
    String key;
    Animation fadein;
    CardView btech_cardView,ca_cardview,ku_cardview,mb_cardview,quick_cardview,diploma_cardview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btechbtn = findViewById(R.id.bt_btech);
        quicksearch=findViewById(R.id.bt_quick_search);
        computer_applications_btn = findViewById(R.id.bt_bca_mca);
        managementbtn = findViewById(R.id.bt_bba_mba);
        kubtn = findViewById(R.id.bt_university_websites);
        total_papers = findViewById(R.id.tv_total_papers);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        btech_cardView=findViewById(R.id.cv_btech);
        ca_cardview=findViewById(R.id.cv_bca_mca);
        ku_cardview=findViewById(R.id.cv_university_websites);
        mb_cardview=findViewById(R.id.cv_bba_mba);
        quick_cardview=findViewById(R.id.cv_quick_search);
        actionbar_signup=findViewById(R.id.bt_sign_up);
        diploma_btn=findViewById(R.id.bt_diploma);
        diploma_cardview = findViewById(R.id.cv_diploma);



        fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        Intent intent=getIntent();
        key=intent.getStringExtra("run counter");
        if(key.equals("yes")){
            startCountAnimation();
            btech_cardView.setAnimation(fadein);
            ca_cardview.setAnimation(fadein);
            ku_cardview.setAnimation(fadein);
            mb_cardview.setAnimation(fadein);
            quick_cardview.setAnimation(fadein);
            diploma_cardview.setAnimation(fadein);

        }
        else
            total_papers.setText("1542");

        Toolbar toolbar = findViewById(R.id.tb_main);
        toolbar.setTitle("");
       // toolbar.setLogo(R.drawable.qslow);
        //toolbar.setBackground(getResources().getDrawable( R.drawable.actionbar_shape));
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nv_main);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


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
        computer_applications_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ComputerApplications_expendable_list.class));
            }
        });
        quicksearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Filters.class));
            }
        });
        diploma_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Diploma_expendable_list.class));
            }
        });
        actionbar_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, RegisterActivity.class));

            }
        });


    }

    public void updateNavHeader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nv_main);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.username);
        TextView navWelcome = headerView.findViewById(R.id.txt_welcome);
        if (mAuth.getCurrentUser() != null)
        {
            navWelcome.setVisibility(View.VISIBLE);
            actionbar_signup.setVisibility(View.INVISIBLE);
            navUsername.setText(mAuth.getCurrentUser().getEmail());
            navigationView.getMenu().findItem(R.id.action_signin).setVisible(false);
            navigationView.getMenu().findItem(R.id.action_logout).setVisible(true);
        }
        else
            {
            navWelcome.setVisibility(View.INVISIBLE);
            actionbar_signup.setVisibility(View.VISIBLE);
            navUsername.setText("Sign in to Share Papers");
            navigationView.getMenu().findItem(R.id.action_signin).setVisible(true);
            navigationView.getMenu().findItem(R.id.action_logout).setVisible(false);
        }
    }
    public void website(View view) {
       startActivity(new Intent(MainActivity.this,Websites.class));
    }
    public void checkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activenetwork = manager.getActiveNetworkInfo();
        if (null != activenetwork) {

            if (activenetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
               // Toast.makeText(this, "Internet connected", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Turn on internet connection", Toast.LENGTH_SHORT).show();

        }
    }
    public void startCountAnimation() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 1542);
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                total_papers.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu2, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.quick:
                startActivity(new Intent(MainActivity.this,Filters.class));
                break;
//
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
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
                if((mAuth.getCurrentUser() == null)) {
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
                                updateNavHeader();
                                Toast.makeText(MainActivity.this, "You have been logged out", Toast.LENGTH_SHORT).show();
                            }
                            if (which == 1) {

                            }
                        }
                    });
                    dialog.create().show();
                    break;
                }
            case R.id.credits:
                startActivity(new Intent(MainActivity.this,CreditActivity.class));
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
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("mailto:" + "qsol.info@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "My contribution to Qsol");
                intent.putExtra(Intent.EXTRA_TEXT, "/* Contribute by\n 1. Attaching previous year exam papers\n 2. Reporting bugs, suggesting features\n 3. Collaborate for maintaining the application */");
                startActivity(intent);
                break;


        }
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
        if (getIntent().getBooleanExtra("EXIT", false))
        {
            finish();
        }
           finish();
        }

    }
    @Override
    protected void onStart() {
        updateNavHeader();
        checkConnection();
        super.onStart();
    }

}
