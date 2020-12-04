package com.aws.takitour.models;

import java.io.Serializable;
import java.util.List;

public class Picture  implements Serializable {
    private List<String> pic;
    private String owner;

    public Picture(List<String> pic, String owner) {
        this.pic = pic;
        this.owner = owner;
    }

    public Picture() {

    }

    public void setPic(List<String> pic) {
        this.pic = pic;
    }

    public List<String> getPic() {
        return pic;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
