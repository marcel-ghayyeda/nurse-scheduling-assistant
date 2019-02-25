package pl.edu.agh.ghayyeda.student.nursescheduling.staff;

import java.util.Objects;

import static pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee.Type.BABY_SITTER;
import static pl.edu.agh.ghayyeda.student.nursescheduling.staff.Employee.Type.NURSE;

public class Employee {

    public enum Type {
        NURSE,
        BABY_SITTER;
    }

    private final String name;
    private final Type type;

    private Employee(String name, Type type) {
        this.name = name;
        this.type = type;
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
