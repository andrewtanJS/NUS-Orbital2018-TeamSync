package com.sync.orbital.calendarsync;

public class ContactsAllStruct {
    String image;
    String name;

    public ContactsAllStruct(){

    }

    public ContactsAllStruct(String image, String name) {
        this.image = image;
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
