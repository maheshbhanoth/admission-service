package pvn.service;

import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import pvn.entity.Admission;
import pvn.entity.ApplicationStatus;
import pvn.repository.AdmissionRepository;

@Service
@Transactional
public class AdmissionService {

	private final AdmissionRepository ar;

	public AdmissionService(AdmissionRepository ar) {
		this.ar = ar;
	}

	private static final Logger log = LoggerFactory.getLogger(AdmissionService.class);

	public Admission applyAdmission(Admission admission) {
		if (admission == null) {
			throw new IllegalArgumentException("Admission cannot be null");
		}

		if (admission.getEmail() == null || admission.getEmail().isBlank()) {
			throw new IllegalArgumentException("Email is required");
		}
		if (ar.findByEmail(admission.getEmail()).isPresent()) {
			throw new IllegalArgumentException("email already exists: " + admission.getEmail());
		}
		admission.setStatus(ApplicationStatus.PENDING);
		log.info("Saving admission for email: {}", admission.getEmail());
		return ar.save(admission);
	}

	@Transactional(readOnly = true)
	public Page<Admission> getAdmissions(Pageable pageable) {
		return ar.findAll(pageable);
	}

	public Admission getAdmissionById(Integer id) {
		return ar.findById(id).orElseThrow(() -> new RuntimeException("Application not found: " + id));
	}

	public Admission updateAdmission(Integer id, ApplicationStatus status) {

		if (id == null) {
			throw new IllegalArgumentException("Admission Id cannot be null");
		}
		if (status == null) {
			throw new IllegalArgumentException("Application status cannot be null");
		}
		Admission admission = ar.findById(id)
				.orElseThrow(() -> new RuntimeException("Admission not found with id: " + id));

		if (admission.getStatus() == ApplicationStatus.REJECTED
				|| admission.getStatus() == ApplicationStatus.APPROVED) {
			throw new IllegalArgumentException("cannot update status for finalized admission");
		}

		// skip update if status is unchanged
		if (admission.getStatus().equals(status)) {
			return admission; // no change need
		}

		admission.setStatus(status);

		return ar.save(admission);
	}

	public void deleteById(Integer id) {
		if (id == null) {
			throw new RuntimeException("Admission id cannot be null");
		}
		if (!ar.existsById(id)) {
			throw new EntityNotFoundException("No admission found with id: " + id);
		}
		ar.deleteById(id);
	}

}
