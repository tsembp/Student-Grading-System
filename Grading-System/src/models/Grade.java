package models;

public class Grade {
    private double midTermGrade;
    private double endTermGrade;

    public Grade(double midTermGrade, double endTermGrade) {
        this.midTermGrade = midTermGrade;
        this.endTermGrade = endTermGrade;
    }

    // Getters and setters
    public double getMidTermGrade() {
        return midTermGrade;
    }

    public void setMidTermGrade(double midTermGrade) {
        this.midTermGrade = midTermGrade;
    }

    public double getEndTermGrade() {
        return endTermGrade;
    }

    public void setEndTermGrade(double endTermGrade) {
        this.endTermGrade = endTermGrade;
    }

    // Calculate weighted average
    public double calculateFinalGrade() {
        return (midTermGrade * 0.4) + (endTermGrade * 0.6);
    }

    @Override
    public String toString() {
        return String.format("Midterm: %.2f, End-term: %.2f, Final: %.2f",
                midTermGrade, endTermGrade, calculateFinalGrade());
    }
}
