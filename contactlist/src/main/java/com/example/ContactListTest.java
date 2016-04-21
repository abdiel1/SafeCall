package com.example;

public class ContactListTest {

    public static void main(String args []){

        ContactListManager contactListManager = ContactListManager.getInstance();

        Contact contact1 = new Contact("Abdie","0000000000");
        Contact contact2 = new Contact("Michelle","1111111111");
        Contact contact3 = new Contact("Kenneth","2222222222");
        Contact contact4 = new Contact("Fernando","3333333333");
        Contact contact5 = new Contact("Nayda","4444444444");

        contactListManager.addContact(contact1);
        contactListManager.addContact(contact2);
        contactListManager.addContact(contact3);
        contactListManager.addContact(contact4);
        contactListManager.addContact(contact5);

        contactListManager.saveContacts();
        print(contactListManager);

        contactListManager.reorderContact(contact1, 3);
        System.out.println();
        print(contactListManager);

        contactListManager.removeContact(contact4);
        contactListManager.saveContacts();
        System.out.println();
        print(contactListManager);


    }

    public static void print(ContactListManager cm){
        for (Contact c : cm.getContactList()) {
            System.out.println(c.toString());
        }
    }
}
