package com.sync.orbital.calendarsync;

public class ContactsAllStruct {
    String image;
    String name;
    String thumb_image;

    public ContactsAllStruct(){

    }

    public ContactsAllStruct(String image, String name) {
        this.image = image;
        this.name = name;
        this.thumb_image = thumb_image;
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

    public String getThumbImage() {
        return thumb_image;
    }

    public void setThumbImage(String thumb_image) {
        this.thumb_image = thumb_image;
    }


}
