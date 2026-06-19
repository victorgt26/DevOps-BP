package devsu.devops.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("User controller")
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private final String relativePath = "/users";

	@Test
	@DisplayName("GET: " + relativePath + ": ✔ Status code 200")
	void getUsers_statusOk() throws Exception {
		mockMvc.perform(
				get(relativePath)
				.characterEncoding("utf-8")
				)
		.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET: missing user returns 404")
	void getMissingUser_statusNotFound() throws Exception {
		mockMvc.perform(get(relativePath + "/99999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errors[0]").value("User not found: 99999"));
	}

	@Test
	@DisplayName("POST: valid user returns 201")
	void createUser_statusCreated() throws Exception {
		mockMvc.perform(post(relativePath)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"dni\":\"0999999998\",\"name\":\"Test User\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.dni").value("0999999998"));
	}

	@Test
	@DisplayName("POST: duplicate DNI returns 400")
	void createDuplicateUser_statusBadRequest() throws Exception {
		mockMvc.perform(post(relativePath)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"dni\":\"0123456789\",\"name\":\"Duplicate User\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0]").value("User already exists"));
	}

	@Test
	@DisplayName("POST: invalid payload returns 400")
	void createInvalidUser_statusBadRequest() throws Exception {
		mockMvc.perform(post(relativePath)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"dni\":\"1\",\"name\":\"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray());
	}
}
