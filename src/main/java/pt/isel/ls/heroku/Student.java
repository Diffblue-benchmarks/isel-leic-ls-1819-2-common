package pt.isel.ls.heroku;

public class Student {

    private final String name;
    private final int number;

    public Student(int number, String name) {

        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }
}
