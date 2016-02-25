package org.owntracks.android.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.owntracks.android.App;
import org.owntracks.android.R;
import org.owntracks.android.support.Events;
import org.owntracks.android.support.SnackbarFactory;

import de.greenrobot.event.EventBus;

public abstract class ActivityBase extends AppCompatActivity implements SnackbarFactory.SnackbarFactoryDelegate {
    private static final String TAG = "ActivityBase";
    Toolbar toolbar;

    @Override
    public View getSnackbarTargetView() {
        return getWindow().getDecorView().findViewById(android.R.id.content);
    }

    boolean hasIntentExtras() {
        return getIntent() != null && getIntent().getExtras() != null;
    }

    private static final int[] PERMISSION_GRANTED = new int[]{PackageManager.PERMISSION_GRANTED};
    private static final String[] PERMISSIONS_PLACEHOLDER = new String[]{""};

    void runActionWithLocationPermissionCheck(int action) {
        runActionWithPermissionCheck(action, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void runActionWithPermissionCheck(int action, String permission) {
        Log.v(TAG, "runActionWithPermissionCheck() " + permission);
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            onRequestPermissionsResult(action, PERMISSIONS_PLACEHOLDER, PERMISSION_GRANTED);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, action);
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            EventBus.getDefault().post(new Events.PermissionGranted(permissions[0])); // Notify about changed permissions

            onRunActionWithPermissionCheck(requestCode, true);
        } else {
            onRunActionWithPermissionCheck(requestCode, false);

        }
    }

    void onRunActionWithPermissionCheck(int action, boolean granted) {

    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);


    }


    public static void launchActivityFromDrawer(@NonNull ActivityBase source, @NonNull Class<ActivityBase> target) {
        Intent i = new Intent(source, target);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );

        source.startActivity(i);

    }
    
    public View getToolbar() {
        return toolbar; 
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
     //   switch (item.getItemId()) {
        //        case android.R.id.home: //
       //         goToRoot();
        //         return true;
        //        }
        //        return false;
    }

    public void onBackPressed() {
        //    if(this.getClass() != App.getRootActivityClass()) {
        //       goToRoot();
        //    }
//
     //   Log.v(TAG, "onBackPressed");
        super.onBackPressed();
       // supportFinishAfterTransition();
    }

    @Override
    public void onStart() {
        overridePendingTransition(R.anim.push_up_in, R.anim.none);
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.none, R.anim.push_down_out);
    }

    void setupSupportToolbar() {
        setupSupportToolbar(true);
    }

    private void setupSupportToolbar(boolean hideTitle) {
        toolbar = (Toolbar) findViewById(R.id.fragmentToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(!hideTitle);
    }

    void goToRoot() {

        Intent i = new Intent(this, App.getRootActivityClass());
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        supportFinishAfterTransition();
       // overridePendingTransition(0, 0);

    }





}
