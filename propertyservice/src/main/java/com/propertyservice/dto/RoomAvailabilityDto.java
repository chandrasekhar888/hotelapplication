package com.propertyservice.dto;

import java.time.LocalDate;


public class RoomAvailabilityDto {
    private long id;

    private LocalDate availableDate;
    private int availableCount;
    private double price;

    private long roomId; // Only include the Room ID, not the entire Room object

	public long getRoomId() {
		return roomId;
	}

	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}

	public long getId() {
		return id;
	}

	public LocalDate getAvailableDate() {
		return availableDate;
	}

	public int getAvailableCount() {
		return availableCount;
	}

	public double getPrice() {
		return price;
	}

	

	public void setId(long id) {
		this.id = id;
	}

	public void setAvailableDate(LocalDate availableDate) {
		this.availableDate = availableDate;
	}

	public void setAvailableCount(int availableCount) {
		this.availableCount = availableCount;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	

    // Getters and Setters...
    
    
    
}