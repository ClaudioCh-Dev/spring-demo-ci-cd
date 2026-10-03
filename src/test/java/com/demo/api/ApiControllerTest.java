package com.demo.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // fresh data per test
class ApiControllerTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	void getStatus() throws Exception {
		mvc.perform(get("/status"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("healthy"));
	}

	@Test
	void getStores() throws Exception {
		mvc.perform(get("/stores"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(5));
	}

	@Test
	void getStore() throws Exception {
		mvc.perform(get("/stores/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("My Awesome Store"));
	}

	@Test
	void getStoreNotFound() throws Exception {
		mvc.perform(get("/stores/999"))
				.andExpect(status().isNotFound());
	}

	@Test
	void addStore() throws Exception {
		mvc.perform(post("/stores")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"My Test Store\",\"address\":\"My test address\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(6));
	}

	@Test
	void addInvalidStore() throws Exception {
		mvc.perform(post("/stores")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"x\",\"address\":\"\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void deleteStore() throws Exception {
		mvc.perform(delete("/stores/1")).andExpect(status().isNoContent());
		mvc.perform(get("/stores/1")).andExpect(status().isNotFound());
	}
}
