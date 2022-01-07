package fact.it.todoedgeservice.controller;

import fact.it.todoedgeservice.model.ListItem;
import fact.it.todoedgeservice.model.ToDoList;
import fact.it.todoedgeservice.model.TodoItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
@RestController
public class TodoController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${listitemservice.baseurl}")
    private String listItemServiceBaseUrl;
    @Value("${todolistservice.baseurl}")
    private String todoListServiceBaseUrl;

    @GetMapping("/todos/user/{userId}")
    public List<TodoItem> getTodosByUserId(@PathVariable String userId){
        List<TodoItem> returnList = new ArrayList<>();
        return returnList;
    }
    @GetMapping("/todos/listitem/{listitemcode}")
    public TodoItem getTodoByListitemcode(@PathVariable String code){
        ListItem listItem = restTemplate.getForObject("http://" + listItemServiceBaseUrl+"/listitems/{code}",
                ListItem.class, code);
        ToDoList toDoList = restTemplate.getForObject("http://"+todoListServiceBaseUrl+"/naam/{naam}",
                ToDoList.class, listItem.getListNaam());
        return new TodoItem(toDoList, listItem);

    }
    @GetMapping("/todos/list/naam/{naam}")
    public TodoItem getTodosByNaam(@PathVariable String naam) {
        ToDoList toDoList = restTemplate.getForObject("http://" + todoListServiceBaseUrl + "/lists/naam/{naam}",
                ToDoList.class, naam);
        ResponseEntity<List<ListItem>> responseEntityListItem =
                restTemplate.exchange("http://" + listItemServiceBaseUrl + "/listitems/list/naam/{naam}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<ListItem>>() {
                        }, naam);


        return new TodoItem(toDoList, responseEntityListItem.getBody());

    }
    @GetMapping("/todos/{userId}/list/{naam}")
    public TodoItem getTodosByUserAndNaam(@PathVariable String naam, @PathVariable int userId){
        ToDoList toDoList = restTemplate.getForObject("http://" + todoListServiceBaseUrl + "/lists/naam/{naam}",
                ToDoList.class, naam);
        ResponseEntity<List<ListItem>> responseEntityListItem =
                restTemplate.exchange("http://" + listItemServiceBaseUrl + "/listitems/user/"+userId+"/list/naam/"+naam,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<ListItem>>() {
                        }, naam);


        return new TodoItem(toDoList, responseEntityListItem.getBody());

    }
    @GetMapping("/todos/list/categorie/{categorie}")
    public List<TodoItem> getTodosByCategorie(@PathVariable String categorie){
        List<TodoItem> returnList = new ArrayList<>();
        ResponseEntity<List<ToDoList>> responseEntityTodoList =
                restTemplate.exchange("http://" + todoListServiceBaseUrl + "/lists/categorie/{categorie}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<ToDoList>>() {
                        }, categorie);
        List<ToDoList> toDoLists = responseEntityTodoList.getBody();
        for (ToDoList toDoList: toDoLists){
            ResponseEntity<List<ListItem>> responseEntityListItem =
                    restTemplate.exchange("http://" + listItemServiceBaseUrl + "/listitems/list/naam/{naam}",
                            HttpMethod.GET, null, new ParameterizedTypeReference<List<ListItem>>() {
                            }, toDoList.getNaam());
            returnList.add(new TodoItem(toDoList, responseEntityListItem.getBody()));

        }
        return returnList;

    }
    @PostMapping("/todos")
    public TodoItem addTodo(@RequestParam String code,@RequestParam Integer userId,@RequestParam String titel, @RequestParam String naam,  @RequestParam String beschrijving, @RequestParam boolean status){
        ListItem listItem = restTemplate.postForObject("http://"+listItemServiceBaseUrl+"/listitems",
                new ListItem(code,userId,titel,naam,beschrijving,status), ListItem.class);
        ToDoList toDoList = restTemplate.getForObject("http://" + todoListServiceBaseUrl + "/lists/naam/{naam}",
                ToDoList.class, naam);
        return  new TodoItem(toDoList, listItem);
    }
    @PutMapping ("/todos")
    public TodoItem updateTodo(@RequestParam String code,@RequestParam String titel, @RequestParam String naam,  @RequestParam String beschrijving, @RequestParam boolean status){
        ListItem listItem = restTemplate.getForObject("http://"+listItemServiceBaseUrl+"/listitems/"+code, ListItem.class);
        listItem.setTitel(titel);
        listItem.setBeschrijving(beschrijving);
        listItem.setStatus(status);
        ResponseEntity<ListItem> listItemResponseEntity =
                restTemplate.exchange("http://" + listItemServiceBaseUrl + "/listitems",
                        HttpMethod.PUT, new HttpEntity<>(listItem), ListItem.class);
        ListItem retrievedListItem = listItemResponseEntity.getBody();
        ToDoList toDoList = restTemplate.getForObject("http://" + todoListServiceBaseUrl + "/lists/naam/{naam}",
                ToDoList.class, naam);
        return  new TodoItem(toDoList, retrievedListItem);
    }
    @DeleteMapping("/todos/listitem/{listitemcode}")
    public ResponseEntity deleteTodo(@PathVariable String code){
        restTemplate.delete("http://" + listItemServiceBaseUrl + "/listitem/"+code);
        return ResponseEntity.ok().build();
    }

}
