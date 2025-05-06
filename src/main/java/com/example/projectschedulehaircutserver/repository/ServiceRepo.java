package com.example.projectschedulehaircutserver.repository;

import com.example.projectschedulehaircutserver.dto.ServiceDTO;
import com.example.projectschedulehaircutserver.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ServiceRepo extends JpaRepository<Service, Integer> {
    @Query("select s from Service s where s.id = :id")
    Optional<Service> findServiceById(@Param("id") Integer id);

    @Query("select new com.example.projectschedulehaircutserver.dto.ServiceDTO(s.id, s.name, s.image, s.price, s.haircutTime) " +
            "from Service s")
    Set<ServiceDTO> findAllService();

    @Query("select s from Service s")
    List<Service> findAllServices();

    @Query("select s from Service s where s.name = :name")
    Optional<Service> findByName(@Param("name") String name);

    @Query(value = "CALL findAllServiceByComboId(:comboId)", nativeQuery = true)
    List<Object[]> findAllServiceByComboId(@Param("comboId") Integer comboId);

    @Query("select new com.example.projectschedulehaircutserver.dto.ServiceDTO(s.id, s.name, s.image, s.price, s.haircutTime) " +
            "from Service s " +
            "JOIN Category c ON c.id = s.category.id " +
            "WHERE c.id = :categoryId")
    Set<ServiceDTO> findAllServiceByCategoryId(@Param("categoryId") Integer categoryId);
}
