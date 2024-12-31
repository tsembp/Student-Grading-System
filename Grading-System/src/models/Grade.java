package models;

public class Grade {

    private double midtermGrade;
    private double endTermGrade;
    private double projectGrade;

    public Grade(double midtermGrade, double endTermGrade, double projectGrade) {
        this.midtermGrade = midtermGrade;
        this.endTermGrade = endTermGrade;
        this.projectGrade = projectGrade;
    }

    public double getMidtermGrade() {
        return midtermGrade;
    }

    public void setMidtermGrade(double midtermGrade) {
        this.midtermGrade = midtermGrade;
    }

    public double getEndTermGrade() {
        return endTermGrade;
    }

    public void setEndTermGrade(double endTermGrade) {
        this.endTermGrade = endTermGrade;
    }

    public double getProjectGrade() {
        return projectGrade;
    }

    public void setProjectGrade(double projectGrade) {
        this.projectGrade = projectGrade;
    }

    public double getFinalGrade() {
        return Math.round((midtermGrade + endTermGrade + projectGrade) / 3 * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return "Midterm: " + midtermGrade + ", End-Term: " + endTermGrade + ", Project: " + projectGrade;
    }
    
}
