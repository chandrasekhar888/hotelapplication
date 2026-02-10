package com.propertyservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertyservice.dto.APIResponse;
import com.propertyservice.dto.PropertyDto;
import com.propertyservice.dto.RoomAvailabilityDto;
import com.propertyservice.entity.RoomAvailability;
import com.propertyservice.entity.Rooms;
import com.propertyservice.repository.RoomAvailabilityRepository;
import com.propertyservice.service.PropertyService;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/property")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;
    @Autowired
    private RoomAvailabilityRepository availabilityRepository;

    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);

    @PostMapping(
        value = "/add-property",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<APIResponse<PropertyDto>> addProperty(
            @RequestParam("property") String propertyJson,
            @RequestParam("files") MultipartFile[] files) {

        logger.info("Property JSON: {}", propertyJson);
        logger.info("Number of files uploaded: {}", (files != null ? files.length : 0));

        ObjectMapper objectMapper = new ObjectMapper();
        PropertyDto dto;
        try {
            dto = objectMapper.readValue(propertyJson, PropertyDto.class);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing property JSON", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        PropertyDto savedPropertyDto = propertyService.addProperty(dto, files);

        APIResponse<PropertyDto> response = new APIResponse<>();
        response.setMessage("Property added");
        response.setStatus(HttpStatus.CREATED.value());
        response.setData(savedPropertyDto);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    //for searching based on query in propertyrepo 
    @GetMapping("/search-property")
	public APIResponse<?> searchProperty(
	        @RequestParam String name,
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	) {
	    APIResponse<?> response = propertyService.searchProperty(name, date);
	    return response;
	}
    @GetMapping("/property-id")
	public APIResponse<PropertyDto> getPropertyById(@RequestParam long id){
		APIResponse<PropertyDto> response = propertyService.findPropertyById(id);
		return response;
	}

	//“We expose availability as date-wise data so booking service can validate multi-day bookings.”
    @GetMapping("/room-available-room-id")
	public APIResponse<List<RoomAvailability>> getTotalRoomsAvailable(@RequestParam long id){
		List<RoomAvailability> totalRooms = propertyService.getTotalRoomsAvailable(id);
		
		APIResponse<List<RoomAvailability>> response = new APIResponse<>();
	    response.setMessage("Total rooms");
	    response.setStatus(200);
	    response.setData(totalRooms);
	    return response;
	}
	//“Booking service fetches pricing dynamically to avoid stale price data.”
	@GetMapping("/room-id")
	public APIResponse<Rooms> getRoomType(@RequestParam long id){
		Rooms room = propertyService.getRoomById(id);
		
		APIResponse<Rooms> response = new APIResponse<>();
	    response.setMessage("Total rooms");
	    response.setStatus(200);
	    response.setData(room);
	    return response;
	}
	//this logic will be called by feignclient Room Booking and Deduction
		@PutMapping("/updateRoomCount") //we will call this in the feign client which is in Bookingservice
		public APIResponse<Boolean> updateRoomCount(@RequestParam long id ,@RequestParam LocalDate date ){
			APIResponse<Boolean> response = new APIResponse<>();
			RoomAvailability roomsAvailable = availabilityRepository.getRooms(id, date);
			int Count = roomsAvailable.getAvailableCount();
			if(Count > 0) {
			roomsAvailable.setAvailableCount(Count-1);
			availabilityRepository.save(roomsAvailable);
			response.setMessage("updated");
			response.setStatus(200);
			response.setData(true);
			
			return response;
			}else {
				response.setMessage("Not Available Room are sold");
				response.setStatus(500);
				response.setData(false);
				
				return response;
				}
			
		}
		//adding room availability to post data
		@PostMapping("/room-availability")
		public ResponseEntity<APIResponse<String>> addRoomAvailability(@RequestBody RoomAvailabilityDto dto) {
		    RoomAvailability roomAvailability = new RoomAvailability();
		    roomAvailability.setAvailableCount(dto.getAvailableCount());
		    roomAvailability.setAvailableDate(dto.getAvailableDate());
		    roomAvailability.setPrice(dto.getPrice());

		    Rooms room = new Rooms();
		    room.setId(dto.getRoomId());
		    roomAvailability.setRoom(room); // just setting FK reference

		    availabilityRepository.save(roomAvailability);

		    APIResponse<String> response = new APIResponse<>();
		    response.setMessage("Room availability added");
		    response.setStatus(201);
		    response.setData("Success");

		    return new ResponseEntity<>(response, HttpStatus.CREATED);
		}

 }


