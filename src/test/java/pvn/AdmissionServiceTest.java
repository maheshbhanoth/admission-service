package pvn;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;

import jakarta.persistence.EntityNotFoundException;
import pvn.entity.Admission;
import pvn.entity.ApplicationStatus;
import pvn.repository.AdmissionRepository;
import pvn.service.AdmissionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class AdmissionServiceTest {

	@Mock
	private AdmissionRepository repo;

	@InjectMocks
	private AdmissionService service;

	private Admission plainData;

	@BeforeEach
	void data() {
		repo = mock(AdmissionRepository.class);
		service = new AdmissionService(repo);

		plainData = new Admission(1, "Mahesh", "mahesh@gmail.com", LocalDateTime.now(), ApplicationStatus.PENDING);
	}

	@Test
	void ApplayAdmission() {
		when(repo.findByEmail("mahesh@gmail.com")).thenReturn(Optional.empty());
		when(repo.save(plainData)).thenReturn(plainData);
		Admission saved=service.applyAdmission(plainData);
		assertThat(saved).isEqualTo(plainData);
		verify(repo).save(plainData);
	}

	@Test
	void applayAdmission_DuplicateEmailThrows() {
		when(repo.findByEmail("mahesh@gmail.com")).thenReturn(Optional.of(plainData));
		assertThatThrownBy(()-> service.applyAdmission(plainData))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("email already exists");
	}

	@Test
	void getAdmissionsTest() {
		Pageable pageable = PageRequest.of(0, 5);
		Page<Admission> page = new PageImpl<>(List.of(plainData), pageable, 1);
		when(repo.findAll(pageable)).thenReturn(page);

		Page<Admission> result = service.getAdmissions(pageable);
		assertThat(result.getContent()).containsExactly(plainData);
		verify(repo).findAll(pageable);
	}

	@Test
	void updateAdmission_invalidTransitionThrows() {
		Admission existing = new Admission(1, "Mahesh", "mahesh@gmail.com", LocalDateTime.now(),
				ApplicationStatus.APPROVED);
		when(repo.findById(1)).thenReturn(Optional.of(existing));

		assertThatThrownBy(() -> service.updateAdmission(1, ApplicationStatus.PENDING))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void deleteAdmissionTest() {
		when(repo.existsById(43)).thenReturn(false);
		assertThatThrownBy(() -> service.deleteById(43))
		.isInstanceOf(EntityNotFoundException.class);
	}
}
