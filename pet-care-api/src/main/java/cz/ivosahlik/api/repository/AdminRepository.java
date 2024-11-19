package cz.ivosahlik.api.repository;

import cz.ivosahlik.api.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
