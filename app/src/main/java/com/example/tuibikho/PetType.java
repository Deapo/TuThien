//Tạo model cho item pet type

package com.example.tuibikho;

public class PetType {
    private final int imgResId;
    private final String name;

    //Constructor
    public PetType(int iconResId, String name) {
        this.imgResId = iconResId;
        this.name = name;
    }

    //Getter
    public int getImgResId() {
        return imgResId;
    }
    public String getName() {return name;}
}
