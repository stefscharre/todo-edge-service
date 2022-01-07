package fact.it.todoedgeservice.model;

public class ToDoList {
    private int id;

    private String naam;
    private String categorie;

    public ToDoList(String naam, String categorie) {
        this.naam = naam;
        this.categorie = categorie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
}
