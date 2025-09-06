package pvn.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "admissions", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Admission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotBlank
	private String studentName;

	@Email
	@NotBlank
	private String email;

	private LocalDateTime submittedAt = LocalDateTime.now();

	@Enumerated(EnumType.STRING)
	private ApplicationStatus status;

	public Admission() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDateTime getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(LocalDateTime submittedAt) {
		this.submittedAt = submittedAt;
	}

	public ApplicationStatus getStatus() {
		return status;
	}

	public void setStatus(ApplicationStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Admission [id=" + id + ", studentName=" + studentName + ", email=" + email + ", submittedAt="
				+ submittedAt + ", status=" + status + "]";
	}

	public Admission(int id, @NotBlank String studentName, @Email @NotBlank String email, LocalDateTime submittedAt,
			ApplicationStatus status) {
		super();
		this.id = id;
		this.studentName = studentName;
		this.email = email;
		this.submittedAt = submittedAt;
		this.status = status;
	}

}
