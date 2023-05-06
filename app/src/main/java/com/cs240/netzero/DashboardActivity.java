package com.cs240.netzero;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class DashboardActivity extends AppCompatActivity {

    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;

    private FloatingActionButton fabAddCo2;
    private FloatingActionButton fabAddMaintenance;
    private FloatingActionButton fabAddExpense;
    private FloatingActionButton fabAddRefuel;
    private FloatingActionButton fabAddTax;
    private FloatingActionButton fabAddInsurance;
    private boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        loadFragment(new HomeFragment());

        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        fabAddMaintenance = findViewById(R.id.fabAddMaintenance);
        fabAddCo2 = findViewById(R.id.fabAddCo2);
        fabAddExpense = findViewById(R.id.fabAddExpense);
        fabAddRefuel = findViewById(R.id.fabAddRefuel);
        fabAddInsurance = findViewById(R.id.fabAddInsurance);
        fabAddTax = findViewById(R.id.fabAddTax);

        NavigationBarView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeNavItem:
                        loadFragment(new HomeFragment());
                        return true;
                    case R.id.expenseListNavItem:
                        loadFragment(new ExpenseListFragment());
                        return true;
                    default:
                        return false;
                }
            }
        });

        fabAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddButtonClick();
            }
        });

        fabAddMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, AddExpenseActivity.class).putExtra("expenseType", "MAINTENANCE"));
            }
        });

        fabAddRefuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, AddExpenseActivity.class).putExtra("expenseType", "REFUEL"));
            }
        });

        fabAddCo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(DashboardActivity.this, AddExpenseActivity.class).putExtra("expenseType", "CO2"));
                startActivity(new Intent(DashboardActivity.this, GetReadingsActivity.class).putExtra("expenseType", "DAILIES"));
            }
        });

        fabAddTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, AddExpenseActivity.class).putExtra("expenseType", "TAX"));
            }
        });

        fabAddInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, AddExpenseActivity.class).putExtra("expenseType", "INSURANCE"));
            }
        });
    }

    private void onAddButtonClick() {
        setVisibility();
        setAnimation();
        setClickable();
        clicked = !clicked;
    }

    private void setVisibility() {
        if (!clicked) {
            fabAddRefuel.setVisibility(View.VISIBLE);
            fabAddCo2.setVisibility(View.VISIBLE);

        } else {
            fabAddMaintenance.setVisibility(View.GONE);
            fabAddCo2.setVisibility(View.INVISIBLE);
            fabAddRefuel.setVisibility(View.INVISIBLE);
            fabAddInsurance.setVisibility(View.GONE);
            fabAddTax.setVisibility((View.GONE));

        }

    }

        private void setAnimation() {
            if (!clicked) {
                fabAddRefuel.startAnimation(fromBottom);
                fabAddCo2.startAnimation(fromBottom);
                fabAddMaintenance.startAnimation(fromBottom);
                fabAddInsurance.startAnimation(fromBottom);
                fabAddTax.startAnimation(fromBottom);
                fabAddExpense.startAnimation(rotateOpen);
            } else {
                fabAddRefuel.startAnimation(toBottom);
                fabAddCo2.startAnimation(toBottom);
                fabAddMaintenance.startAnimation(toBottom);
                fabAddInsurance.startAnimation(toBottom);
                fabAddTax.startAnimation(toBottom);
                fabAddExpense.startAnimation(rotateClose);
            }
        }

        private void setClickable() {
            if (!clicked) {
                fabAddRefuel.setClickable(true);
                fabAddCo2.setClickable(true);
                fabAddMaintenance.setClickable(true);
                fabAddInsurance.setClickable(true);
                fabAddTax.setClickable(true);
            } else {
                fabAddRefuel.setClickable(false);
                fabAddCo2.setClickable(false);
                fabAddMaintenance.setClickable(false);
                fabAddInsurance.setClickable(false);
                fabAddTax.setClickable(false);
            }
        }

        private void loadFragment(Fragment fragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.dashboardContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }
