package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

public enum EmployeeAvailability {
    FULL_TIME("Full time"),
    PART_TIME("Part time");

    private final String label;

    EmployeeAvailability(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
