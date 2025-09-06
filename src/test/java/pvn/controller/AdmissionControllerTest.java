package pvn.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import pvn.entity.Admission;
import pvn.entity.ApplicationStatus;
import pvn.service.AdmissionService;

@WebMvcTest(AdmissionController.class)
public class AdmissionControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	AdmissionService service;

	@Autowired
	ObjectMapper mapper;

	private Admission app;

	@BeforeEach
	void setup() {
		app = new Admission(1, "Mahesh", "mahesh@gmail.com", LocalDateTime.now(), ApplicationStatus.APPROVED);
	}

	@Test
	void applyAdmission() throws Exception{
		when(service.applyAdmission(any(Admission.class))).thenReturn(app);
		
		mockMvc.perform(post("/admission")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(app)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.email").value("mahesh@gmail.com"));
	}

	@Test
	void getAdmissionsTest() throws Exception {
		PageRequest pr = PageRequest.of(0, 10);
		Page<Admission> page = new PageImpl<>(List.of(app), pr, 1);

		when(service.getAdmissions(pr)).thenReturn(page);

		mockMvc.perform(get("/admission").param("page", "0").param("size", "10")).andExpect(status().isOk());
	}

	@Test
	void updateStatusTest() throws Exception {
		Admission existing = new Admission(1, "Mahesh", "mahesh@gmail.com", LocalDateTime.now(),
				ApplicationStatus.PENDING);
		Admission updated = new Admission(1, "Mahesh", "mahesh@gmail.com", LocalDateTime.now(),
				ApplicationStatus.APPROVED);

		when(service.getAdmissionById(1)).thenReturn(existing);
		when(service.updateAdmission(1, ApplicationStatus.APPROVED)).thenReturn(updated);

		mockMvc.perform(put("/admission/1").param("status", "APPROVED")).andExpect(status().isAccepted())
				.andExpect(jsonPath("$.status").value("APPROVED"));
	}

	@Test
	void deleteAdmission() throws Exception {
		doNothing().when(service).deleteById(1);

		mockMvc.perform(delete("/admission/1")).andExpect(status().isNoContent());
	}

}
