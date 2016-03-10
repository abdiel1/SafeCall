package com.example.abdielrosado.safecall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import ContactManagement.ContactList;
import ContactManagement.ContactListManager;

/**
 * Created by abdielrosado on 3/9/16.
 */
public class MoveContactsDialogFragment extends DialogFragment {


    public interface MoveContactsDialogListener{
        public void onDialogPositiveClick(int position);
        public void onDialogNegativeClick();
    }

    private MoveContactsDialogListener moveContactsDialogListener;

    public MoveContactsDialogFragment(){
        super();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try {
            moveContactsDialogListener = (MoveContactsDialogListener) activity;
        } catch(ClassCastException e){
            throw new ClassCastException(activity.toString() +
                    "must implement MoveContactsDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstances){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Get Layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //Set view with inflated layout
        //Pass null because parent will be the dialog layout
        View view = inflater.inflate(R.layout.position_change, null);


        //Set number picker parameters
        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
        final ContactList contactList = ContactListManager.getInstance();
        numberPicker.setMaxValue(contactList.size());
        numberPicker.setMinValue(1);

        builder.setView(view);


        //Set positive listener
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
               moveContactsDialogListener.onDialogPositiveClick(numberPicker.getValue());
            }
        });

        //Set negative listener
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveContactsDialogListener.onDialogNegativeClick();
            }
        });


        return builder.create();
    }
}
