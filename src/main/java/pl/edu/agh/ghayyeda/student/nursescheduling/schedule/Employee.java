package pl.edu.agh.ghayyeda.student.nursescheduling.schedule;

import java.util.Objects;

import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Employee.Type.BABY_SITTER;
import static pl.edu.agh.ghayyeda.student.nursescheduling.schedule.Employee.Type.NURSE;

public class Employee {

    public enum Type {
        NURSE("Nurse"),
        BABY_SITTER("Baby sitter");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private final String name;
    private final Type type;

    private Employee(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public static Employee employee(String name, Type type) {
        return new Employee(name, type);
    }

    public static Employee nurse(String name) {
        return new Employee(name, NURSE);
    }

    public static Employee babySitter(String name) {
        return new Employee(name, BABY_SITTER);
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String getId() {
        return name + type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(name, employee.name) &&
                type == employee.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
