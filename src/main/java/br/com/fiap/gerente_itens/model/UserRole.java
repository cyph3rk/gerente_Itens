package br.com.fiap.gerente_itens.model;

public enum UserRole {
    ADMIN("admin"),
    USER("user"),
    CLIENTE("cliente");

    private final String role;

    UserRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
