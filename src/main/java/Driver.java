
class Driver {
    private String FIO;
    private double experience;
    private String classification;
    private String violations;
    final static String[] s_classification = {"M", "0", "1", "2", "3", "4", "5", "6", "7", "9", "9", "10",
            "11", "12", "13"};

    Driver(String FIO, double experience, String classification, String violations) {
        this.FIO = FIO;
        this.experience = experience;
        this.classification = classification;
        this.violations = violations;
    }

    Driver(String FIO, double experience, String classification) {
        this.FIO = FIO;
        this.experience = experience;
        this.classification = classification;
    }

    String getFIO() {
        return FIO;
    }

    double getExperience() {
        return experience;
    }

    String getClassification() {
        return classification;
    }

    void setFIO(String FIO) {
        this.FIO = FIO;
    }

    void setExperience(double experience) {
        this.experience = experience;
    }

    void setClassification(String classification) {
        this.classification = classification;
    }

    String getViolations() {
        return violations;
    }

    void setViolations(String violations) {
        this.violations = violations;
    }

}
