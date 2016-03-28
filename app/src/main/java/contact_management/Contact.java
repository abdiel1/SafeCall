package contact_management;

/**
 * Created by abdielrosado on 3/2/16.
 * This class represents a phone contact.
 */
public class Contact {

    /**
     * The contact's name.
     */
    private String name;

    /**
     * The contact's phone number.
     */
    private String phoneNumber;

    /**
     * Constructor.
     * @param name  Contact's name
     * @param phoneNumber  Contact's phone number.
     */
    public Contact(String name, String phoneNumber){
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Get the contact's name.
     * @return Contact's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the contact's name.
     * @param name Contact's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the contact's phone number.
     * @param phoneNumber Contact's phone number.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Get the contact's phone number.
     * @return Contact's phone number.
     */
    public String getPhoneNumber() {

        return phoneNumber;
    }

    /**
     * Represent the contact object as a string.
     * @return Contact's representation as a string.
     */
    public String toString(){

        return this.name + ": " + this.phoneNumber;
    }

    @Override
    public boolean equals(Object o){

        if(o != null){
            if(this == o){
                return true;
            } else if(o instanceof Contact){ // Check if o is an instance of Contact
                Contact c = (Contact) o;

                // Check if this and o have the same name and number
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
