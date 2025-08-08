# Sistema de Registro

## Descrição

Este é um sistema de registro de alunos baseado em console, desenvolvido em Java. A aplicação permite o gerenciamento de dados de alunos, incluindo a criação, leitura, atualização e exclusão de registros (CRUD). O sistema possui dois tipos de usuários: **Administrador** e **Aluno**, cada um com diferentes níveis de acesso e funcionalidades.

### Funcionalidades

* **Administrador:**
    * Inserir, atualizar, listar e deletar alunos;
    * Criar e listar turmas;
    * Matricular alunos em turmas.

* **Aluno:**
    * Visualizar seus próprios dados cadastrais;
    * Listar as turmas em que está matriculado.

## Tecnologias Utilizadas

* **Linguagem:** Java (JDK 23);
* **Banco de Dados:** PostgreSQL;
* **Build:** Maven;
* **Dependências:**
    * `org.postgresql:postgresql:42.7.3`

## Práticas Adotadas

* Programação Orientada a Objetos (POO);
* DAO (Data Access Object);
* Connection Factory;
* Tratamento de Exceções;
* Estrutura de Pacotes.

## Como Executar

1.  **Pré-requisitos:**
    * JDK 23 ou superior;
    * Maven;
    * Um servidor de banco de dados PostgreSQL em execução.

2.  **Configuração do Banco de Dados:**
    * Crie um banco de dados no PostgreSQL;
    * Atualize as credenciais de conexão (`URL`, `USER`, `PASSWORD`) no arquivo `src/main/java/util/ConnectionFactory.java`.

3.  **Execução:**
    * Clone o repositório;
    * Navegue até o diretório raiz do projeto;
    * Execute a aplicação a partir da classe `Main.java`.

Ao iniciar, a aplicação oferecerá a opção de criar as tabelas `alunos` e `usuarios` e inserir um usuário administrador padrão (`login: admin1`, `senha: 1234`).
