package com.hospitalApi.medicines.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hospitalApi.medicines.models.SaleMedicine;

public interface SaleMedicineRepository extends JpaRepository<SaleMedicine, String> {

    public List<SaleMedicine> findByConsultId(String consultId);

    public List<SaleMedicine> findByMedicineId(String medicineId);

    @Query("""
                SELECT SUM(sm.price * sm.quantity)
                FROM  SaleMedicine sm
                WHERE sm.consult.id = :consultId
            """)
    public Double totalSalesMedicinesByConsult(@Param("consultId") String consultId);

    public List<SaleMedicine> findAll();

    @Query("""
             SELECT s FROM SaleMedicine s
             WHERE (:name IS NULL OR LOWER(s.employee.firstName) LIKE LOWER(CONCAT('%', :name, '%')))
               AND (:cui IS NULL OR LOWER(s.employee.cui) LIKE LOWER(CONCAT('%', :cui, '%')))
            """)

    public List<SaleMedicine> findAllByEmployeeNameAndCui(
            @Param("name") String name,
            @Param("cui") String cui);

    @Query("SELECT SUM(sm.price * sm.quantity) FROM SaleMedicine sm WHERE sm.medicine.id = :medicineId")
    public Double totalSalesMedicinesByMedicine(@Param("medicineId") String medicineId);

    @Query("""
                SELECT SUM(sm.price * sm.quantity)
                FROM SaleMedicine sm
                WHERE sm.medicine.id = :medicineId
                AND sm.createdAt BETWEEN :startDate AND :endDate
            """)
    public Double totalSalesMedicinesByMedicineBetweenDates(
            @Param("medicineId") String medicineId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    public List<SaleMedicine> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate);

    @Query("""
            SELECT s FROM SaleMedicine s
            WHERE (:startDate IS NULL OR s.createdAt >= :startDate)
            AND (:endDate IS NULL OR s.createdAt <= :endDate)
            AND (:medicineName IS NULL OR LOWER(s.medicine.name) LIKE LOWER(CONCAT('%', :medicineName, '%')))
                    """)
    public List<SaleMedicine> findByCreatedAtBetweenAndMedicineNameLike(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("medicineName") String medicineName);

    @Query("""
                SELECT SUM(sm.price * sm.quantity)
                FROM SaleMedicine sm
                WHERE sm.createdAt BETWEEN :startDate AND :endDate
            """)
    public Double totalSalesMedicinesBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    public boolean existsByConsultId(String consultId);

}
