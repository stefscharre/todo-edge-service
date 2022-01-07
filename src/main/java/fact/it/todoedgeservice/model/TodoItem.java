package fact.it.todoedgeservice.model;

import java.util.ArrayList;
import java.util.List;

public class TodoItem {
    private String listNaam;
    private List<UserItem> userItems;

    public TodoItem(ToDoList toDoList, List<ListItem> listItems){
        setListNaam(toDoList.getNaam());
        userItems = new ArrayList<>();
        listItems.forEach(listItem ->{
            userItems.add(new UserItem(listItem.getListItemCode(),listItem.getUserId(),
                    listItem.getTitel(), listItem.getBeschrijving()));
        });
        setUserItems(userItems);
    }
    public TodoItem(ToDoList toDoList, ListItem listItem){
        setListNaam(toDoList.getNaam());
        userItems = new ArrayList<>();
            userItems.add(new UserItem(listItem.getListItemCode(),listItem.getUserId(),
                    listItem.getTitel(), listItem.getBeschrijving()));

    }

    public String getListNaam() {
        return listNaam;
    }

    public void setListNaam(String listNaam) {
        this.listNaam = listNaam;
    }

    public List<UserItem> getUserItems() {
        return userItems;
    }

    public void setUserItems(List<UserItem> userItems) {
        this.userItems = userItems;
    }
}
