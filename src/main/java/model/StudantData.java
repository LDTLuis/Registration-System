package model;

import java.sql.Date;

public class StudantData {
    private int id;
    private String nome;
    private String matricula;
    private String telefone;
    private Date dataNascimento;
    private Courses curso;
    private String CPF;

    public StudantData(String nome, String matricula, String telefone, Date dataNascimento, Courses curso, String CPF) {
        this.nome = nome;
        this.matricula = matricula;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.curso = curso;
        this.CPF = CPF;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getTelefone() {
        return telefone;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public Courses getCurso() {
        return curso;
    }

    public String getCPF() {
        return CPF;
    }
}
