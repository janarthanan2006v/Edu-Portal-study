package com.prince.materialportal.repository;

import com.prince.materialportal.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    Optional<Material> findBySubjectNameIgnoreCaseAndActiveStatusTrue(String subjectName);

    List<Material> findBySubjectNameContainingIgnoreCaseAndActiveStatusTrue(String subjectName);

    List<Material> findByDepartmentIgnoreCaseAndActiveStatusTrue(String department);

    List<Material> findByDepartmentIgnoreCase(String department);

    List<Material> findByCourseYearIgnoreCaseAndActiveStatusTrue(String courseYear);

    List<Material> findAllByOrderByUpdatedAtDesc();

    long countByActiveStatus(Boolean activeStatus);

    @Query("SELECT m FROM Material m WHERE LOWER(TRIM(m.subjectName)) = LOWER(TRIM(:subjectName)) AND m.activeStatus = true")
    Optional<Material> findExactActiveBySubjectName(@Param("subjectName") String subjectName);
}
