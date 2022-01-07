package fact.it.todoedgeservice.model;

public class UserItem {
    private Integer userId;
    private String titel;
    private String beschrijving;

    public UserItem(Integer userId, String titel, String beschrijving) {
        this.userId = userId;
        this.titel = titel;
        this.beschrijving = beschrijving;
    }
}
