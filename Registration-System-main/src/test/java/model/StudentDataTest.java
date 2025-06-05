package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StudentDataTest {

    private StudentData student;
    private final String nome = "John Doe";
    private final String matricula = "202301001";
    private final String telefone = "999888777";
    private final Date dataNascimento = Date.valueOf(LocalDate.of(2000, 5, 15));
    private final Courses curso = Courses.ENGENHARIA_DE_SOFTWARE;
    private final String cpf = "123.456.789-00";
    private final int id = 1;

    @BeforeEach
    void setUp() {
        student = new StudentData(nome, matricula, telefone, dataNascimento, curso, cpf);
    }

    @Test
    void constructorShouldSetAllFields() {
        assertEquals(nome, student.getNome());
        assertEquals(matricula, student.getMatricula());
        assertEquals(telefone, student.getTelefone());
        assertEquals(dataNascimento, student.getDataNascimento());
        assertEquals(curso, student.getCurso());
        assertEquals(cpf, student.getCPF());
    }

    @Test
    void setIdShouldSetId() {
        student.setId(id);
        assertEquals(id, student.getId());
    }

    @Test
    void setMatriculaShouldUpdateMatricula() {
        String novaMatricula = "202402002";
        student.setMatricula(novaMatricula);
        assertEquals(novaMatricula, student.getMatricula());
    }

    @Test
    void getNomeShouldReturnNome() {
        assertEquals(nome, student.getNome());
    }

    @Test
    void getMatriculaShouldReturnMatricula() {
        assertEquals(matricula, student.getMatricula());
    }

    @Test
    void getTelefoneShouldReturnTelefone() {
        assertEquals(telefone, student.getTelefone());
    }

    @Test
    void getDataNascimentoShouldReturnDataNascimento() {
        assertEquals(dataNascimento, student.getDataNascimento());
    }

    @Test
    void getCursoShouldReturnCurso() {
        assertEquals(curso, student.getCurso());
    }

    @Test
    void getCPFShouldReturnCPF() {
        assertEquals(cpf, student.getCPF());
    }

    @Test
    void getIdShouldReturnInitialZeroIfNotSet() {
        StudentData newStudent = new StudentData("Jane", "M002", "111", Date.valueOf("2001-01-01"), Courses.DIREITO, "11122233344");
        assertEquals(0, newStudent.getId());
    }
}