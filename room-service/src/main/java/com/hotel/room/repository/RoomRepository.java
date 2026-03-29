package com.hotel.room.repository;

import com.hotel.room.model.Room;
import com.hotel.room.model.RoomStatus;
import com.hotel.room.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomNumber(String roomNumber);
    List<Room> findByStatus(RoomStatus status);
    List<Room> findByRoomType(RoomType roomType);
    List<Room> findByStatusAndRoomType(RoomStatus status, RoomType roomType);
    boolean existsByRoomNumber(String roomNumber);
}
