package pvn.controller;

import org.springframework.data.domain.Pageable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pvn.entity.Admission;
import pvn.entity.ApplicationStatus;
import pvn.service.AdmissionService;

@RestController
@RequestMapping("/admission")
@CrossOrigin("*")
public class AdmissionController {

	private final AdmissionService as;

	public AdmissionController(AdmissionService as) {
		this.as = as;
	}

	private static final Logger log = LoggerFactory.getLogger(AdmissionController.class);

	@PostMapping
	public ResponseEntity<Admission> apply(@Valid @RequestBody Admission admission) {
		try {
			Admission ad = as.applyAdmission(admission);
			return ResponseEntity.status(HttpStatus.CREATED).body(ad);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		}
	}

	@GetMapping
	public ResponseEntity<Page<Admission>> getAdmissions(Pageable pageable) {
		log.info("GET /admission called with page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
		Page<Admission> page = as.getAdmissions(pageable);

		log.info("Fetched {} admissions from the database (page {}, size {})", page.getTotalElements(),
				pageable.getPageNumber(), pageable.getPageSize());

		return ResponseEntity.ok(page);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Admission> getAdmissionById(@PathVariable Integer id) {
		Admission ad = as.getAdmissionById(id);
		log.info("Received request for admission with ID: {}", id);
		return ResponseEntity.ok(ad);
	}

	
	@PutMapping("/{id}")
	public ResponseEntity<Admission> updateAdmissionStatus(@PathVariable Integer id,
	        @RequestParam(required = false) ApplicationStatus status) {
	    
	    Admission current = as.getAdmissionById(id);
	    
	    if (!current.getStatus().canTransitionTo(status)) {
	        log.warn("Attempted invalid status update: {} -> {}", current.getStatus(), status);
	        throw new IllegalArgumentException("Invalid status transition");
	    }

	    Admission updated = as.updateAdmission(id, status);
	    return ResponseEntity.status(HttpStatus.ACCEPTED).body(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void	> deleteAdmission(@PathVariable Integer id) {
		log.info("Request to delete admission with ID: {}", id);
		as.deleteById(id);
		log.info("Successfully deleted admission with ID: {}", id);
		return ResponseEntity.noContent().build();
	}
}
