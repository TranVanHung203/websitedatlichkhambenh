package tlcn.quanlyphongkham.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

import tlcn.quanlyphongkham.entities.HoSoBenh;
import tlcn.quanlyphongkham.dtos.HoSoBenhDTO;

@Repository
public interface HoSoBenhRepository extends JpaRepository<HoSoBenh, String> {
    @Query("SELECT new tlcn.quanlyphongkham.dtos.HoSoBenhDTO(hs.hoSoId, bn.ten, hs.chanDoan, hs.thoiGianTao, t.ten) " +
           "FROM HoSoBenh hs " +
           "JOIN hs.benhNhan bn " +
           "JOIN hs.donThuocs dt " +
           "JOIN dt.donThuocThuocs dtt " +
           "JOIN dtt.thuoc t " +
           "WHERE hs.bacSi.id = :bacSiId " +
           "AND hs.thoiGianTao BETWEEN :startDateTime AND :endDateTime")
    List<HoSoBenhDTO> findHoSoBenhWithDetails(@Param("startDateTime") LocalDateTime startDateTime, 
                                              @Param("endDateTime") LocalDateTime endDateTime, 
                                              @Param("bacSiId") String bacSiId);
}
