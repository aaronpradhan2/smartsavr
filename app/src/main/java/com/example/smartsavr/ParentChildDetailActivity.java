package com.example.smartsavr;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

public class ParentChildDetailActivity extends AppCompatActivity implements ModifyAllowanceBottomSheetDialog.OnSaveListener {

    private static final String TAG = "ParentChildDetailActivity";

    static DBReference childDBReference;
    FirebaseFirestore firebaseFirestore;
    static CollectionReference collectionReference;

    static Child child;



    public static final long WEEK_IN_MILLIS = 604800000;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_child_detail);

        Intent intent = getIntent();
        child = (Child) intent.getSerializableExtra(Utils.CHILD);

        Log.d(TAG, "Creating activity now");
        Log.d(TAG, "Child: " + child);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(child.getName() + "'s Profile");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("children");
        childDBReference = new DBReference(collectionReference,firebaseFirestore);
        setListners();



    }

    private void giveAllowance() {
        Log.d("ParentChildDetail", "Giving allowance");
        Log.d("current time", "" + System.currentTimeMillis());
        Log.d("last allowance time", "" + child.getLastAllowanceTime());
        Log.d("week in millis", "" + WEEK_IN_MILLIS);
        int weeksSinceLastAllowance = (int) ((System.currentTimeMillis() - child.getLastAllowanceTime()) / WEEK_IN_MILLIS);
        Log.d("ParentChildDetail", "Weeks since last allowance: " + weeksSinceLastAllowance);
        if (weeksSinceLastAllowance > 0) {
            child.setAccountBalanceCents(child.getAccountBalanceCents() + child.getWeeklyAllowanceCents() * weeksSinceLastAllowance);
            LocalDateTime localDate = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY));
            ZonedDateTime zonedDateTime = localDate.atZone(ZoneId.of("America/New_York"));
            child.setLastAllowanceTime(zonedDateTime.toInstant().toEpochMilli());
        }

        childDBReference.collectionReference.whereEqualTo("username", child.getUsername()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (Child child : querySnapshot.toObjects(Child.class)) {
                        ParentChildDetailActivity.childDBReference.collectionReference.document(child.getId()).set(this.child);
                    }
                }
            }
        });
    }

    private void setClickListeners(Child child) {
        Button modifyBalance = findViewById(R.id.modify_balance);
        modifyBalance.setOnClickListener(view -> {
            ModifyAllowanceBottomSheetDialog bottomSheet = new ModifyAllowanceBottomSheetDialog(child);
            bottomSheet.show(getSupportFragmentManager(), ModifyAllowanceBottomSheetDialog.TAG);
        });

        Button manageChores = findViewById(R.id.manage);
        manageChores.setOnClickListener(view -> {
            Intent intent = new Intent(this, ParentChildChoresActivity.class);
            intent.putExtra(Utils.CHILD, child);
            startActivity(intent);
        });

        Button allowanceSummary = findViewById(R.id.allowance_summary);
        allowanceSummary.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChildSummaryActivity.class);
            intent.putExtra(Utils.CHILD, child);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_edit_profile) {
            Intent myIntent = new Intent(this, AddChildActivity.class);
            myIntent.putExtra(Utils.CHILD, child);
            startActivity(myIntent);
            return true;
        } else if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "Restarting now!");
        recreate();
    }

    @Override
    public void acceptBalance(int balanceCents) {
        child.setAccountBalanceCents(balanceCents);
        TextView currentBalanceTV = findViewById(R.id.current_balance);
        currentBalanceTV.setText("Account Balance: " + Utils.centsToDollarString(child.getAccountBalanceCents()));
    }

    private void setListners() {
        firebaseFirestore.collection("chores").whereEqualTo("childID",child.getId()).whereEqualTo("approved",true).addSnapshotListener(
                (value, error) -> {
                    if (error != null) {

                        System.err.println("Listen failed: " + error);
                        return;
                    }
                   int  earningCents = 0;
                    for (DocumentSnapshot ds : value.getDocuments()) {
                        //TODO handle exception
                        Chore obj = ds.toObject(Chore.class);
                        earningCents += obj.getRewardCents();

                    }
                    Log.d("EARNING CENTS",Integer.toString(earningCents));

                    setListner2();
                }
        );



    }

    private void setListner2(){
        firebaseFirestore.collection("chores").whereEqualTo("childID",child.getId()).whereEqualTo("complete",true).addSnapshotListener(
                (value, error) -> {
                    if (error != null) {

                        System.err.println("Listen failed: " + error);
                        return;
                    }
                    int tasksCompleted = value.getDocuments().size();
                    child.setChoresCompleted(tasksCompleted);
                    giveAllowance();


                    ImageView logo = findViewById(R.id.logo);
                    logo.setImageResource(Utils.getImageResource(child.getProfilePicture()));

                    //Having an issue with string resources here
                    TextView choresCompletedTV = findViewById(R.id.chores_completed);
                    choresCompletedTV.setText("Chores Completed: "  + child.getChoresCompleted());

                    TextView currentBalanceTV = findViewById(R.id.current_balance);
                    currentBalanceTV.setText("Account Balance: " + Utils.centsToDollarString(child.getAccountBalanceCents()));

                    TextView allowanceTV = findViewById(R.id.allowance);
                    allowanceTV.setText("Allowance: " + Utils.centsToDollarString(child.getWeeklyAllowanceCents()) + " per week*");

                    TextView nameTV = findViewById(R.id.child_name);
                    nameTV.setText(child.getName());

                    setClickListeners(child);

                }
        );
    }
}
