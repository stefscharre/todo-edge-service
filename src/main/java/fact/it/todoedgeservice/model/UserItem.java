package fact.it.todoedgeservice.model;

public class UserItem {
    private String listitemcode;
    private Integer userId;
    private String titel;
    private String beschrijving;


    public UserItem(String listitemcode,Integer userId, String titel, String beschrijving) {
        this.listitemcode=listitemcode;
        this.userId = userId;
        this.titel = titel;
        this.beschrijving = beschrijving;
    }

    public String getListitemcode() {
        return listitemcode;
    }

    public void setListitemcode(String listitemcode) {
        this.listitemcode = listitemcode;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }
}
