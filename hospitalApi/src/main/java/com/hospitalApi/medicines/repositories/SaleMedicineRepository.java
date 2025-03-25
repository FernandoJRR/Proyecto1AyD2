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
    Double totalSalesMedicinesByConsult(@Param("consultId") String consultId);

    public List<SaleMedicine> findAll();

    @Query("SELECT SUM(sm.price * sm.quantity) FROM SaleMedicine sm WHERE sm.medicine.id = :medicineId")
    Double totalSalesMedicinesByMedicine(@Param("medicineId") String medicineId);

    @Query("""
                SELECT SUM(sm.price * sm.quantity)
                FROM SaleMedicine sm
                WHERE sm.medicine.id = :medicineId
                AND sm.createdAt BETWEEN :startDate AND :endDate
            """)
    Double totalSalesMedicinesByMedicineBetweenDates(
            @Param("medicineId") String medicineId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<SaleMedicine> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate);

    @Query("""
                SELECT SUM(sm.price * sm.quantity)
                FROM SaleMedicine sm
                WHERE sm.createdAt BETWEEN :startDate AND :endDate
            """)
    Double totalSalesMedicinesBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

}
