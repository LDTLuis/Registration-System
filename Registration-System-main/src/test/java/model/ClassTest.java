package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClassTest {

    @Test
    void defaultConstructorShouldCreateInstance() {
        Class turma = new Class();
        assertNotNull(turma);
        assertNull(turma.getNome());
        assertNull(turma.getCurso());
        assertEquals(0, turma.getId());
    }

    @Test
    void constructorWithNomeAndCursoShouldSetFields() {
        String nome = "Turma A";
        Courses curso = Courses.CIENCIA_DA_COMPUTACAO;
        Class turma = new Class(nome, curso);

        assertEquals(nome, turma.getNome());
        assertEquals(curso, turma.getCurso());
        assertEquals(0, turma.getId());
    }

    @Test
    void constructorWithIdNomeAndCursoShouldSetFields() {
        int id = 101;
        String nome = "Turma B";
        Courses curso = Courses.MEDICINA;
        Class turma = new Class(id, nome, curso);

        assertEquals(id, turma.getId());
        assertEquals(nome, turma.getNome());
        assertEquals(curso, turma.getCurso());
    }

    @Test
    void setIdShouldSetId() {
        Class turma = new Class();
        int id = 5;
        turma.setId(id);
        assertEquals(id, turma.getId());
    }

    @Test
    void setNomeShouldSetNome() {
        Class turma = new Class();
        String nome = "Introduction to Algorithms";
        turma.setNome(nome);
        assertEquals(nome, turma.getNome());
    }

    @Test
    void setCursoShouldSetCurso() {
        Class turma = new Class();
        Courses curso = Courses.MATEMATICA;
        turma.setCurso(curso);
        assertEquals(curso, turma.getCurso());
    }

    @Test
    void toStringShouldReturnCorrectFormat() {
        int id = 1;
        String nome = "Software Engineering I";
        Courses curso = Courses.ENGENHARIA_DE_SOFTWARE;
        Class turma = new Class(id, nome, curso);
        String expected = "Turma " + id + " | " + nome + " | " + "Curso: " + curso.getNomeCurso();
        assertEquals(expected, turma.toString());
    }

    @Test
    void toStringShouldHandleNullCurso() {
        int id = 2;
        String nome = "General Physics";
        Class turma = new Class(id, nome, null);
        String expected = "Turma " + id + " | " + nome + " | " + "Curso: null";
        assertEquals(expected, turma.toString());
    }
}