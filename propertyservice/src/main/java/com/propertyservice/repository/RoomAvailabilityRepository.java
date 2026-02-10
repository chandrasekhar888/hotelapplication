package com.propertyservice.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.propertyservice.entity.RoomAvailability;

public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

	public List<RoomAvailability> findByRoomId(long id);
	
	@Query("SELECT ra FROM RoomAvailability ra WHERE ra.room.id = :roomId AND ra.availableDate = :date")
	public RoomAvailability getRooms(@Param("roomId") long id, @Param("date") LocalDate date); //for availability


}