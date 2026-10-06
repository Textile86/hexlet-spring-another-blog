package io.hexlet.spring;

import com.jayway.jsonpath.JsonPath;
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
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listPublished_returns200_andPage() throws Exception {
        mockMvc.perform(get("/api/posts").param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void show_unknownPost_returns404() throws Exception {
        mockMvc.perform(get("/api/posts/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_withShortFields_returns422_json() throws Exception {
        var body = """
            {"title":"ab","content":"short"}
            """;
        mockMvc.perform(post("/api/posts").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    void create_withoutUserId_returns422_json() throws Exception {
        var body = """
            {"title":"Пост без автора","content":"Достаточно длинный текст для валидации"}
            """;
        mockMvc.perform(post("/api/posts").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.userId").exists());
    }

    @Test
    void create_withUnknownAuthor_returns404() throws Exception {
        var body = """
            {"title":"Пост несуществующего автора","content":"Достаточно длинный текст","userId":9999}
            """;
        mockMvc.perform(post("/api/posts").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPost_returns201_andAppearsInPublishedList() throws Exception {
        var body = """
            {"title":"Новый пост","content":"Достаточно длинный текст для валидации","userId":1}
            """;

        String json = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.published").value(true))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(json, "$.id");

        mockMvc.perform(get("/api/posts").param("size", "100").param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id));
    }

    @Test
    void updatePost_keepsSameId_returns200() throws Exception {
        var body = """
            {"title":"Обновлённый заголовок","content":"Обновлённый достаточно длинный текст"}
            """;

        String before = mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Boolean publishedBefore = JsonPath.read(before, "$.published");

        mockMvc.perform(put("/api/posts/1").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Обновлённый заголовок"))
                .andExpect(jsonPath("$.published").value(publishedBefore));
    }

    @Test
    void updatePost_canUnpublish_returns200() throws Exception {
        var body = """
            {"title":"Черновик","content":"Достаточно длинный текст черновика","published":false}
            """;

        mockMvc.perform(put("/api/posts/2").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.published").value(false));
    }

    @Test
    void update_unknownPost_returns404() throws Exception {
        var body = """
            {"title":"Обновление несуществующего","content":"Обновлённый достаточно длинный текст"}
            """;

        mockMvc.perform(put("/api/posts/9999").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePost_returns204_then404() throws Exception {
        var body = """
            {"title":"Пост под удаление","content":"Достаточно длинный текст для валидации","userId":1}
            """;

        String json = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(json, "$.id");

        mockMvc.perform(delete("/api/posts/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/posts/" + id))
                .andExpect(status().isNotFound());
    }
}
