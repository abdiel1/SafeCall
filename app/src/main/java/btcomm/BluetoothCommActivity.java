package btcomm;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.abdielrosado.safecall.MainActivity;
import com.example.abdielrosado.safecall.R;

public class BluetoothCommActivity extends AppCompatActivity implements DeviceListFragment.OnFragmentInteractionListener{

    public static int REQUEST_BLUETOOTH = 1;

    private BluetoothAdapter blueAdapter;
    private DeviceListFragment mDeviceListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_comm);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        blueAdapter = BluetoothAdapter.getDefaultAdapter();
        //Phone does not support Bluetooth so let the user know and exit
        if(blueAdapter == null){
            new AlertDialog.Builder(this)
                    .setTitle("Not Compatible")
                    .setMessage("Your smartphone does not support Bluetooth Communication")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        if(!blueAdapter.isEnabled()){
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        mDeviceListFragment = DeviceListFragment.newInstance(blueAdapter);

        fragmentManager.beginTransaction().replace(R.id.container, mDeviceListFragment).commit();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.home:
                startActivity(new Intent(this,MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onFragmentInteraction(String id) {
        SendAndGetData.getInstance(getApplicationContext()).start(id);
        finish();
    }
}
