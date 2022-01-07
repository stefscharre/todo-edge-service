package fact.it.todoedgeservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fact.it.todoedgeservice.model.ListItem;
import fact.it.todoedgeservice.model.ToDoList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoEdgeControllerUnitTests {

    @Value("${listitemservice.baseurl}")
    private String listItemServiceBaseUrl;
    @Value("${todolistservice.baseurl}")
    private String todoListServiceBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    private ToDoList list1 = new ToDoList("school", "Rood");
    private ToDoList list2 = new ToDoList("tuin", "Geel");

    private ListItem listItemUser1List1 = new ListItem("0001", 1, "taak engels", "school", "afmaken", false);
    private ListItem listItemUser2List1 = new ListItem("0002", 2, "taak frans", "school", "frans afmaken", false);
    private ListItem listItemUser2List2 = new ListItem("0003", 2, "haag snoeien", "tuin", "7 uur", false);

    private List<ListItem> allListitemFromUser2 = Arrays.asList(listItemUser2List1, listItemUser2List2);
    private List<ListItem> allListitemFromUser1 = Arrays.asList(listItemUser1List1);
    private List<ListItem> allListitemsForList1 = Arrays.asList(listItemUser1List1, listItemUser2List1);
    private List<ListItem> allListitemsForList2 = Arrays.asList(listItemUser2List2);
    private List<ToDoList> allLists = Arrays.asList(list1, list2);

    @BeforeEach
    public void initializeMockserver() throws URISyntaxException, JsonProcessingException {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        /*//GET listitem from User 1 of list 1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + listItemServiceBaseUrl + "/todos/1/list/school")))
               .andExpect(method(HttpMethod.GET))
               .andRespond(withStatus(HttpStatus.OK)
                       .contentType(MediaType.APPLICATION_JSON)
                       .body(mapper.writeValueAsString(listItemUser1List1))
                );

        // GET all listitems from user 1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + listItemServiceBaseUrl + "/todos/user/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allListitemFromUser1))
               );
        // GET all listitems from user 2
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://" + listItemServiceBaseUrl + "/todos/user/2")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allListitemFromUser2))
                );

        // GET all listitems for list 1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + todoListServiceBaseUrl + "/todos/list/naam/school")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allListitemsForList1))
                );

        // GET all listitems for list 2
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + todoListServiceBaseUrl + "/todos/list/naam/tuin")))
               .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allListitemsForList2))
                );

        // GET List 1 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + todoListServiceBaseUrl + "/lists/naam/school")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(list1))
                );

        // GET List 2 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + todoListServiceBaseUrl + "/lists/naam/tuin")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(list2))
                );

        ListItem listItemUser3List1 = new ListItem("0001", 3, "taak react", "school", "afmaken", false);
        // POST listitem for list1 from User 3
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + listItemServiceBaseUrl + "/listitems")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(listItemUser3List1))
                );
        ListItem updatedlistItemUser1List1 = new ListItem("0001", 1, "taak engels", "school", "afmaken", true);
        // PUT listitem from user 1 for list 1 with new status
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + listItemUser2List1 + "/listitems")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(updatedlistItemUser1List1))
                );

        // DELETE listitem from User 999 of list with school
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + listItemServiceBaseUrl + "/listitems/0009")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK)
                );*/
    }

    @Test
    public void whenGetTodosByUserId_thenReturnTodosJson() throws Exception {

        // GET all reviews from User 2
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://" + listItemServiceBaseUrl + "/todos/user/2")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allListitemFromUser2))
                );

        // GET List 1 info
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://" + todoListServiceBaseUrl + "/lists/naam/school")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(list1))
                );

        // GET List 2 info
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://" + todoListServiceBaseUrl + "/lists/naam/tuin")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(list2))
                );

        mockMvc.perform(get("/todos/user/{userId}", 1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].naam", is("school")))
                .andExpect(jsonPath("$[0].categorie", is("Rood")))
                .andExpect(jsonPath("$[0].userItems[0].listitemcode", is("0002")))
                .andExpect(jsonPath("$[0].userItems[0].userId", is(2)))
                .andExpect(jsonPath("$[0].userItems[0].titel", is("frans")))
                .andExpect(jsonPath("$[0].userItems[0].beschrijving", is("afmaken")))
                .andExpect(jsonPath("$[1].naam", is("tuin")))
                .andExpect(jsonPath("$[1].categorie", is("Geel")))
                .andExpect(jsonPath("$[0].userItems[0].listitemcode", is("0003")))
                .andExpect(jsonPath("$[1].userItems[0].userId", is(2)))
                .andExpect(jsonPath("$[1].userItems[0].titel", is("haag snoeien")))
                .andExpect(jsonPath("$[1].userItems[0].beschrijving", is("7 uur")));
    }

    @Test
    public void whenGetTodoByNaam_thenReturnTodoJson() throws Exception {
// GET list 1 info
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://" + todoListServiceBaseUrl + "/lists/naam/school")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(list1))
                );

        // GET all listitems for list 1
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://" + todoListServiceBaseUrl + "/todos/list/naam/school")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allListitemsForList1))
                );


        mockMvc.perform(get("/todos/list/naam/{naam}", "school"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].naam", is("school")))
                .andExpect(jsonPath("$[0].categorie", is("Rood")))
                .andExpect(jsonPath("$[0].userItems[0].listitemcode", is("0001")))
                .andExpect(jsonPath("$[0].userItems[0].userId", is(1)))
                .andExpect(jsonPath("$[0].userItems[0].titel", is("engels")))
                .andExpect(jsonPath("$[0].userItems[0].beschrijving", is("afmaken")))
                .andExpect(jsonPath("$[0].userItems[1].listitemcode", is("0002")))
                .andExpect(jsonPath("$[0].userItems[1].userId", is(2)))
                .andExpect(jsonPath("$[0].userItems[1].titel", is("frans")))
                .andExpect(jsonPath("$[0].userItems[1].beschrijving", is("afmaken")));

    }


    @Test
    public void whenGetTodosByUserIdAndNaam_thenReturnTodosJson() throws Exception {

        // GET list 1 info
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://" + todoListServiceBaseUrl + "/lists/naam/school")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(list1))
                );

        //GET listitem from User 1 of list 1
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://" + listItemServiceBaseUrl + "/todos/1/list/school")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(listItemUser1List1))
                );

        mockMvc.perform(get("/todos/{userId}/list/{naam}", 1, "school"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naam", is("school")))
                .andExpect(jsonPath("$.categorie", is("Rood")))
                .andExpect(jsonPath("$.userItem[0].listitemcode", is("0001")))
                .andExpect(jsonPath("$.userItem[0].userId", is(1)))
                .andExpect(jsonPath("$.userItem[0].titel", is("engels")))
                .andExpect(jsonPath("$.userItem[0].beschrijving", is("afmaken")));
    }

    @Test
    public void whenAddtodo_thenReturnTodosJson() throws Exception {

        ListItem listItemUser3List1 = new ListItem("0001", 3, "taak react", "school", "afmaken", false);

        // POST listitem for list1 from User 3
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://" + listItemServiceBaseUrl + "/listitems")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(listItemUser3List1))
                );

        // GET list 1 info
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://" + todoListServiceBaseUrl + "/lists/naam/school")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(list1))
                );

        mockMvc.perform(post("/todos")
                        .param("listitemcode", listItemUser3List1.getListItemCode().toString())
                        .param("userid", listItemUser3List1.getUserId().toString())
                        .param("titel", listItemUser3List1.getTitel().toString())
                        .param("beschrijving", listItemUser3List1.getBeschrijving().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naam", is("school")))
                .andExpect(jsonPath("$.categorie", is("Rood")))
                .andExpect(jsonPath("$.userItems[0].listitemcode", is("0001")))
                .andExpect(jsonPath("$.userItems[0].userId", is(3)))
                .andExpect(jsonPath("$.userItems[0].titel", is("react")))
                .andExpect(jsonPath("$.userItems[0].beschrijving", is("afmaken")));
    }

    @Test
    public void whenUpdateTodo_thenReturnTodosJson() throws Exception {

        ListItem updatedlistItemUser1List1 = new ListItem("0001", 1, "taak engels", "school", "afmaken", true);

        //GET listitem from User 1 of list 1
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://" + listItemServiceBaseUrl + "/todos/1/list/school")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(listItemUser1List1))
                );

        // PUT listitem from user 1 for list 1 with new status
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://" + listItemUser2List1 + "/listitems")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(updatedlistItemUser1List1))
                );

        // GET list 1 info
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://" + todoListServiceBaseUrl + "/lists/naam/school")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(list1))
                );

        mockMvc.perform(post("/todos")
                        .param("listitemcode", updatedlistItemUser1List1.getListItemCode().toString())
                        .param("userid", updatedlistItemUser1List1.getUserId().toString())
                        .param("titel", updatedlistItemUser1List1.getTitel().toString())
                        .param("beschrijving", updatedlistItemUser1List1.getBeschrijving().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle", is("Book1")))
                .andExpect(jsonPath("$.isbn", is("ISBN1")))
                .andExpect(jsonPath("$.userItems[0].userId", is(1)))
                .andExpect(jsonPath("$.userItems[0].scoreNumber", is(5)));

    }

    @Test
    public void whenDeletetodo_thenReturnStatusOk() throws Exception {

        // DELETE listitem from User 999 of list with school
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("http://" + listItemServiceBaseUrl + "/listitems/0009")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK)
                );

        mockMvc.perform(delete("/todos/listitem/{listitemcode}", "0009"))
                .andExpect(status().isOk());
    }
}