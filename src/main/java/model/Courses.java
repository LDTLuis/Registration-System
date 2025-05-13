package model;

public enum Courses {
        ENGENHARIA_DE_SOFTWARE(1,"Engenharia de Software"),
        SISTEMA_DA_INFORMCAO(2,"Sistemas de Informacao" ),
        CIENCIA_DA_COMPUTACAO(3,"Ciencia da Computacao" ),
        ENGENHARIA_CIVIL(4,"Engenharia Civil" ),
        ENGENHARIA_DE_MATEMATICA(5,"Engenharia de Matematica" ),
        BIOMEDICINA(6,"Biomedicina" ),
        MEDICINA(7,"Medicina" ),
        MATEMATICA(8,"Matematica" ),
        FISICA(9,"Fisica" ),
        DIREITO(10,"Direito" ),
        PSICOLOGIA(11,"Psicologia" ),
        FARMACIA(12,"Farmacia" );

        private final int id;
        private final String nomeCurso;

    Courses(int id, String nomeCurso) {
        this.id = id;
        this.nomeCurso = nomeCurso;
    }

    public String getNomeCurso() {
            return nomeCurso;
    }

    public int getId() {
        return id;
    }

    public static Courses getById(int id){
        for(Courses c : Courses.values()){
            if(c.getId() == id){
                return c;
            }
        }
        return null;
    }

    public static Courses getByNomeCurso(String nome) {
        for (Courses c : values()) {
            if (c.nomeCurso.equalsIgnoreCase(nome)) {
                return c;
            }
        }
        return null;
    }
}
