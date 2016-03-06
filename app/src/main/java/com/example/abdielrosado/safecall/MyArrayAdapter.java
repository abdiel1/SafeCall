package com.example.abdielrosado.safecall;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import ContactManagement.Contact;

import java.util.List;

/**
 * Created by abdielrosado on 3/5/16.
 */
public class MyArrayAdapter extends ArrayAdapter{

    private final Context context;
    private final List<Contact> list;
    private final int layout;

    public MyArrayAdapter(Context context, List<Contact> list,int layout){
        super(context,-1,list);
        this.context = context;
        this.list = list;
        this.layout = layout;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(this.layout,parent,false);

        TextView name = (TextView) view.findViewById(R.id.contactName);
        TextView number = (TextView) view.findViewById(R.id.phoneNumber);
        ImageView contactPicture = (ImageView) view.findViewById(R.id.imageView);

        name.setText(list.get(position).getName());
        number.setText(list.get(position).getPhoneNumber());

        if(this.layout == R.layout.select_contact){
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    AddContactsActivity.addSelection(position);
                }
            });
        }


        return view;
    }
}
