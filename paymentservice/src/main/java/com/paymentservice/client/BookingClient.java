package com.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "BOOKINGSERVICE")
public interface BookingClient {

	
	@PutMapping("/api/v1/booking/update-status-booking")
	public boolean updateBooking(@RequestParam long id);
}
