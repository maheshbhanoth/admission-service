package pvn.repository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import pvn.entity.Admission;

public interface AdmissionRepository extends JpaRepository<Admission, Integer>{

	Optional<Admission> findByEmail(String email);

	Page<Admission> findAll(Pageable pageable);
}
