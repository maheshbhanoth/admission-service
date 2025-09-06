package pvn.entity;

public enum ApplicationStatus {

	PENDING, APPROVED, REJECTED;
	public boolean canTransitionTo(ApplicationStatus target) {
	    return this == PENDING && (target == APPROVED || target == REJECTED);
	}
	
}
