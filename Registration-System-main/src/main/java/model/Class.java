package model;

public class Class {
    private int id;
    private String nome;
    private Courses curso;

    public Class() {
    }

    public Class(String nome, Courses curso) {
        this.nome = nome;
        this.curso = curso;
    }

    public Class(int id, String nome, Courses curso) {
        this.id = id;
        this.nome = nome;
        this.curso = curso;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Courses getCurso() {
        return curso;
    }

    public void setCurso(Courses curso) {
        this.curso = curso;
    }

    @Override
    public String toString() {
        return "Turma " +
                id + " | " +
                nome + " | " +
                "Curso: " + (curso != null ? curso.getNomeCurso() : "null");
    }
}