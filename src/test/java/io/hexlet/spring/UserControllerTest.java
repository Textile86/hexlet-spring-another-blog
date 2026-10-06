package io.hexlet.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void index_returns200_andList() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createUser_returns201_andBody() throws Exception {
        var body =
                """
                    {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john@example.com"
                    }
                """;

        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void createUser_withBirthday_returns201_andDate() throws Exception {
        var body =
                """
                    {
                    "firstName": "Jane",
                    "lastName": "Roe",
                    "email": "jane.roe@example.com",
                    "birthday": "1995-04-12"
                    }
                """;

        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.birthday").value("1995-04-12"));
    }

    @Test
    void createUser_withInvalidData_returns422_json() throws Exception {
        var body =
                """
                    {
                    "firstName": "",
                    "lastName": "",
                    "email": "not-an-email"
                    }
                """;

        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists());
    }

    @Test
    void show_unknownUser_returns404() throws Exception {
        mockMvc.perform(get("/api/users/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_keepsSameId_returns200() throws Exception {
        var body =
                """
                    {
                    "firstName": "Ivan",
                    "lastName": "Petrov",
                    "email": "updated.user@example.com",
                    "birthday": "1990-05-05"
                    }
                """;

        mockMvc.perform(put("/api/users/2").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("updated.user@example.com"))
                .andExpect(jsonPath("$.lastName").value("Petrov"))
                .andExpect(jsonPath("$.birthday").value("1990-05-05"));
    }

    @Test
    void deleteUserWithPosts_returns409() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isConflict());
    }

    // UserControllerTest: PUT без birthday не должен затирать старое значение
    @Test
    void updateUser_withoutBirthday_keepsPrevious() throws Exception {
        String before = mockMvc.perform(get("/api/users/3")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Object birthdayBefore = com.jayway.jsonpath.JsonPath.read(before, "$.birthday");

        var body = """
        {"firstName":"Ivan","lastName":"Petrov","email":"keep.birthday@example.com"}
        """;
        var result = mockMvc.perform(put("/api/users/3")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("keep.birthday@example.com"))
                .andReturn().getResponse().getContentAsString();

        // JsonPath.read возвращает generic <T>, поэтому сначала присваиваем Object —
        // иначе вызов assertThat(...) двусмысленен (Comparable vs AssertDelegateTarget)
        Object birthdayAfter = com.jayway.jsonpath.JsonPath.read(result, "$.birthday");
        org.assertj.core.api.Assertions.assertThat(birthdayAfter).isEqualTo(birthdayBefore);
    }

    // UserControllerTest: @Email продолжает работать на PUT
    @Test
    void updateUser_withInvalidEmail_returns422() throws Exception {
        var body = """
        {"firstName":"Ivan","lastName":"Petrov","email":"not-an-email"}
        """;
        mockMvc.perform(put("/api/users/3").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.email").exists());
    }

}
