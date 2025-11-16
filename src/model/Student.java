package model;

public class Student {
    private int id;
    private String name;
    private String email;
    private String program;

    public Student(int id, String name, String email, String program) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.program = program;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getProgram() { return program; }
}
