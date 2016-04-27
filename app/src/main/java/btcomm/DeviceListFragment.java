package btcomm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.abdielrosado.safecall.R;

import java.util.ArrayList;
import java.util.Set;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class DeviceListFragment extends Fragment implements AbsListView.OnItemClickListener{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "DEVICELIST";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnFragmentInteractionListener mListener;

    private ArrayList<DeviceItem> deviceItemsList;
    private static BluetoothAdapter blueAdapter;

    //The fragment's ListView/GridView
    private AbsListView mListView;

    private ArrayAdapter<DeviceItem> mAdapter;

    private IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DeviceListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DeviceListFragment newInstance(BluetoothAdapter adapter) {
        DeviceListFragment fragment = new DeviceListFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
//        fragment.setArguments(args);

        blueAdapter = adapter;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
//        }

        Log.d(TAG, "Super called for DeviceListFragment onCreate \n");

        deviceItemsList = new ArrayList<DeviceItem>();

        try {
            //get device
            Set<BluetoothDevice> pairedDevices = blueAdapter.getBondedDevices();

            if(pairedDevices.size() > 0){
                for (BluetoothDevice device : pairedDevices){
                    DeviceItem newDevice = new DeviceItem(device.getName(),device.getAddress(),"false");
                    deviceItemsList.add(newDevice);
                }
            }
            //If there are no devices, add an item that says so.
            if(deviceItemsList.size() == 0){
                deviceItemsList.add(new DeviceItem("No devices", "", "false"));
            }

            Log.d(TAG, "DeviceList populated\n");

            mAdapter = new DeviceListAdapter(getActivity(), deviceItemsList, blueAdapter);



            Log.d(TAG, "Adapter created\n");
        }
        catch(Exception e){
            Log.e(TAG, e.toString());
        }
     }

    private final BroadcastReceiver blueReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                Log.d(TAG, "Bluetooth device was found!");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //Create a new Device
                DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), "false");
                //Add it to the adapter
                mAdapter.add(newDevice);
                mAdapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deviceitem_list, container, false);
        ToggleButton scan = (ToggleButton) view.findViewById(R.id.scan);
        if(blueAdapter.isEnabled()){
            mAdapter.clear();
            getActivity().registerReceiver(blueReceiver, filter);
            blueAdapter.startDiscovery();
            scan.setChecked(true);
        }

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        //Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        scan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mAdapter.clear();
                    getActivity().registerReceiver(blueReceiver, filter);
                    blueAdapter.startDiscovery();
                }
                else {
                    getActivity().unregisterReceiver(blueReceiver);
                    blueAdapter.cancelDiscovery();
                }
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick position: " + position + " id: " + id + " name: " + deviceItemsList.get(position).getDeviceName() + "\n");
        if(null != mListener){
            mListener.onFragmentInteraction(deviceItemsList.get(position).getDeviceName());
            getActivity().unregisterReceiver(blueReceiver);
            blueAdapter.cancelDiscovery();
        }
        //uuid = "00001101-0000-1000-8000-00805F9B34FB"
    }

    //If you would like to change the text for the Fragment, call this method to supply the text it should use

    public void setEmptyText(CharSequence emptyText){
        View emptyView = mListView.getEmptyView();

        if(emptyView instanceof TextView){
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String id);
    }
}
