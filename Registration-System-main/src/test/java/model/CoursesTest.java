package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CoursesTest {

    @Test
    void getNomeCursoShouldReturnCorrectName() {
        assertEquals("Engenharia de Software", Courses.ENGENHARIA_DE_SOFTWARE.getNomeCurso());
        assertEquals("Medicina", Courses.MEDICINA.getNomeCurso());
    }

    @Test
    void getIdShouldReturnCorrectId() {
        assertEquals(1, Courses.ENGENHARIA_DE_SOFTWARE.getId());
        assertEquals(7, Courses.MEDICINA.getId());
    }

    @ParameterizedTest
    @CsvSource({
            "1, ENGENHARIA_DE_SOFTWARE",
            "2, SISTEMA_DA_INFORMCAO",
            "3, CIENCIA_DA_COMPUTACAO",
            "4, ENGENHARIA_CIVIL",
            "5, ENGENHARIA_DE_MATEMATICA",
            "6, BIOMEDICINA",
            "7, MEDICINA",
            "8, MATEMATICA",
            "9, FISICA",
            "10, DIREITO",
            "11, PSICOLOGIA",
            "12, FARMACIA"
    })
    void getByIdShouldReturnCorrectCourse(int id, Courses expectedCourse) {
        assertEquals(expectedCourse, Courses.getById(id));
    }

    @Test
    void getByIdShouldReturnNullForInvalidId() {
        assertNull(Courses.getById(0));
        assertNull(Courses.getById(13));
        assertNull(Courses.getById(-1));
    }

    @ParameterizedTest
    @CsvSource({
            "Engenharia de Software, ENGENHARIA_DE_SOFTWARE",
            "engenharia de software, ENGENHARIA_DE_SOFTWARE",
            "Sistemas de Informacao, SISTEMA_DA_INFORMCAO",
            "CIENCIA DA COMPUTACAO, CIENCIA_DA_COMPUTACAO",
            "Farmacia, FARMACIA"
    })
    void getByNomeCursoShouldReturnCorrectCourse(String nome, Courses expectedCourse) {
        assertEquals(expectedCourse, Courses.getByNomeCurso(nome));
    }

    @Test
    void getByNomeCursoShouldReturnNullForInvalidName() {
        assertNull(Courses.getByNomeCurso("Curso Inexistente"));
        assertNull(Courses.getByNomeCurso(""));
        assertNull(Courses.getByNomeCurso(null));
    }

    @ParameterizedTest
    @CsvSource({
            "Engenharia de Software, ENGENHARIA_DE_SOFTWARE",
            "Sistemas de Informacao, SISTEMA_DA_INFORMCAO",
            "CIENCIA_DA_COMPUTACAO, CIENCIA_DA_COMPUTACAO", // Handles underscore
            "   Engenharia Civil  , ENGENHARIA_CIVIL" // Handles trim
    })
    void getByNomeCursosShouldReturnCorrectCourse(String nomeCurso, Courses expectedCourse) {
        assertEquals(expectedCourse, Courses.getByNomeCursos(nomeCurso));
    }

    @Test
    void getByNomeCursosShouldReturnNullForInvalidFormattedName() {
        assertNull(Courses.getByNomeCursos("EngenhariaSoftwareSemEspaco")); // Requires exact match after formatting
        assertNull(Courses.getByNomeCursos("Unknown Course"));
        assertNull(Courses.getByNomeCursos(null));
    }

    @ParameterizedTest
    @EnumSource(Courses.class)
    void ensureAllCoursesHaveUniqueIds(Courses course) {
        int count = 0;
        for (Courses c : Courses.values()) {
            if (c.getId() == course.getId()) {
                count++;
            }
        }
        assertEquals(1, count, "ID " + course.getId() + " is not unique for " + course.name());
    }

    @ParameterizedTest
    @EnumSource(Courses.class)
    void ensureAllCoursesHaveUniqueNomesCurso(Courses course) {
        int count = 0;
        for (Courses c : Courses.values()) {
            if (c.getNomeCurso().equalsIgnoreCase(course.getNomeCurso())) {
                count++;
            }
        }
        assertEquals(1, count, "NomeCurso '" + course.getNomeCurso() + "' is not unique for " + course.name());
    }
}