package model;

public abstract class Users {
    protected String login;
    protected String senha;

    public Users(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    public abstract void showMenu();
}
