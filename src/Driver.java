import java.lang.reflect.Array;
import java.util.Arrays;

public class Driver {
    private String FIO;
    private int experience;
    private int classification;
    private String[] violations;
    final static int[] s_classification = {'M', 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};

    public Driver(String FIO, int experience, int classification, String[] violations) {
        this.FIO = FIO;
        this.experience = experience;
        this.classification = classification;
        this.violations = violations;
    }

    public Driver(String FIO, int experience, int classification) {
        this.FIO = FIO;
        this.experience = experience;
        this.classification = classification;
    }

    public String getFIO() {
        return FIO;
    }

    public int getExperience() {
        return experience;
    }

    public int getClassification() {
        return classification;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void setClassification(int classification) {
        this.classification = classification;
    }

    public String[] getViolations() {
        return violations;
    }

    public void setViolations(String[] violations) {
        this.violations = violations;
    }

    public static boolean checkClassification(int classification) {
        for (int i : s_classification) {
            if (i == classification) {
                return true;
            }
        }
        return false;
    }
}
