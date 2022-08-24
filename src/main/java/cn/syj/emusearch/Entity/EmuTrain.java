package cn.syj.emusearch.Entity;

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
