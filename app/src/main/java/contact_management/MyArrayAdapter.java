package contact_management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abdielrosado.safecall.R;

import java.util.List;

/**
 * Created by abdielrosado on 3/5/16.
 */
public class MyArrayAdapter extends ArrayAdapter{

    private final Context context;
    private final List<Contact> list;
    private final int layout;
    private final short action;

    public static final short ADDING_CONTACTS = 0;
    public static final short REMOVING_CONTACTS = 1;
    public static final short NONE = 2;

    public MyArrayAdapter(Context context, List<Contact> list,int layout,short action){
        super(context,-1,list);
        this.context = context;
        this.list = list;
        this.layout = layout;
        this.action = action;

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

        if (this.layout == R.layout.select_contact) {
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

            switch (this.action) {
                case ADDING_CONTACTS:
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                AddContactsActivity.addSelection(position);
                            } else{
                                AddContactsActivity.removeSelection(position);
                            }

                        }
                    });
                break;

                case REMOVING_CONTACTS:
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                RemoveContactsActivity.addSelection(position);
                            } else{
                                RemoveContactsActivity.removeSelection(position);
                            }
                        }
                    });
            }

        }


        return view;
    }
}
