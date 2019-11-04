
public class Driver {
    private String FIO;
    private double experience;
    private String classification;
    private String violations;
    final static String[] s_classification = {"M", "0", "1", "2", "3", "4", "5", "6", "7", "9", "9", "10",
            "11", "12", "13"};

    public Driver(String FIO, double experience, String classification, String violations) {
        this.FIO = FIO;
        this.experience = experience;
        this.classification = classification;
        this.violations = violations;
    }

    public Driver(String FIO, double experience, String classification) {
        this.FIO = FIO;
        this.experience = experience;
        this.classification = classification;
    }

    public String getFIO() {
        return FIO;
    }

    public double getExperience() {
        return experience;
    }

    public String getClassification() {
        return classification;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getViolations() {
        return violations;
    }

    public void setViolations(String violations) {
        this.violations = violations;
    }

}
