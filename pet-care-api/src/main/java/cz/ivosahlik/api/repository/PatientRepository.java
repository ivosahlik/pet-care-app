package cz.ivosahlik.api.repository;

import cz.ivosahlik.api.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
