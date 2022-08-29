package cn.syj.emusearch.entity;

import java.util.List;
import java.util.Vector;

/**
 * @author syj
 **/
public class EmuTrain {

    private String model;

    private String number;

    private String bureau;

    private String department;

    private String plant;

    private String description;

    public EmuTrain() {
    }

    public EmuTrain(String model, String number, String bureau, String department, String plant, String description) {
        this.model = model;
        this.number = number;
        this.bureau = bureau;
        this.department = department;
        this.plant = plant;
        this.description = description;
    }

    public EmuTrain(List<?> list) {
        if (list == null || list.size() < 6)
            throw new RuntimeException();
        this.model = (String) list.get(0);
        this.number = (String) list.get(1);
        this.bureau = (String) list.get(2);
        this.department = (String) list.get(3);
        this.plant = (String) list.get(4);
        this.description = (String) list.get(5);
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBureau() {
        return bureau;
    }

    public void setBureau(String bureau) {
        this.bureau = bureau;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Vector<Object> toVector() {
        Vector<Object> v = new Vector<>();
        v.add(this.getModel());
        v.add(this.getNumber());
        v.add(this.getBureau());
        v.add(this.getPlant());
        v.add(this.getDepartment());
        v.add(this.getDescription());
        return v;
    }

    @Override
    public String toString() {
        return "EmuTrain{" +
                "model='" + model + '\'' +
                ", number='" + number + '\'' +
                ", bureau='" + bureau + '\'' +
                ", department='" + department + '\'' +
                ", plant='" + plant + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
