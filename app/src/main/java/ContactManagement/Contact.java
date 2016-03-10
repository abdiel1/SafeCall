package ContactManagement;

import java.util.Objects;

/**
 * Created by abdielrosado on 3/2/16.
 */
public class Contact {

    private String name;

    private String phoneNumber;

    public Contact(String name, String phoneNumber){
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getPhoneNumber() {

        return phoneNumber;
    }

    public String toString(){

        return this.name + ": " + this.phoneNumber;
    }

    @Override
    public boolean equals(Object o){

        if(o != null){
            if(this == o){
                return true;
            } else if(o instanceof Contact){
                Contact c = (Contact) o;
                if(c.getName().equals(this.getName()) && c.getPhoneNumber().equals(this.getPhoneNumber())){
                    return true;
                }
            } else{
                return false;
            }
        }
        return false;
    }



}
