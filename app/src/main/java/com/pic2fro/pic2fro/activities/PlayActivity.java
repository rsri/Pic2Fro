package com.pic2fro.pic2fro.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pic2fro.pic2fro.R;
import com.pic2fro.pic2fro.controller.PlayAdapter;

public class PlayActivity extends AppCompatActivity {

    private PlayAdapter playAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        playAdapter = new PlayAdapter(this);
        playAdapter.beginSlideshow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.player_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            Toast.makeText(this, "Under construction", Toast.LENGTH_SHORT).show();
            System.out.println("Save");
            return true;
        } else if (id == R.id.action_share) {
            Toast.makeText(this, "Under construction", Toast.LENGTH_SHORT).show();
            System.out.println("Share");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        playAdapter.endSlideshow();
        super.onBackPressed();
    }
}
