public class StudentRecord {
    private String course;
    private double score;
    private String grade;
    private int credits;

    public StudentRecord(String course, double score, String grade, int credits) {
        this.course = course;
        this.score = score;
        this.grade = grade;
        this.credits = credits;
    }

    public String getCourse() { return course; }
    public double getScore() { return score; }
    public String getGrade() { return grade; }
    public int getCredits() { return credits; }
}
