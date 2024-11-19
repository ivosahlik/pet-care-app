package cz.ivosahlik.api.repository;

import cz.ivosahlik.api.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
