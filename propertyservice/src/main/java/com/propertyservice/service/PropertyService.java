	package com.propertyservice.service;

import com.propertyservice.constants.AppConstants;
import com.propertyservice.dto.APIResponse;
import com.propertyservice.dto.EmailRequest;
import com.propertyservice.dto.PropertyDto;
import com.propertyservice.dto.RoomsDto;
import com.propertyservice.entity.*;
import com.propertyservice.repository.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

@Service

public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private PropertyPhotosRepository propertyPhotosRepository;
    @Autowired
    private RoomAvailabilityRepository availabilityRepository;
    @Autowired
    private KafkaTemplate<String, EmailRequest> kafkaTemplate;
    @Autowired
    private S3Service s3Service;
    

    public PropertyDto addProperty(PropertyDto dto, MultipartFile[] files) {
        Area area = areaRepository.findByName(dto.getArea());
        City city = cityRepository.findByName(dto.getCity());
        State state = stateRepository.findByName(dto.getState());

        Property property = new Property();
        property.setName(dto.getName());
        property.setNumberOfBathrooms(dto.getNumberOfBathrooms());
        property.setNumberOfBeds(dto.getNumberOfBeds());
        property.setNumberOfRooms(dto.getNumberOfRooms());
        property.setNumberOfGuestAllowed(dto.getNumberOfGuestAllowed());
        property.setArea(area);
        property.setCity(city);
        property.setState(state);

        Property savedProperty = propertyRepository.save(property);

        for (RoomsDto roomsDto : dto.getRooms()) {
            Rooms rooms = new Rooms();
            rooms.setProperty(savedProperty);
            rooms.setRoomType(roomsDto.getRoomType());
            rooms.setBasePrice(roomsDto.getBasePrice());
            roomRepository.save(rooms);
        }
        // ✅ Generate PDF with property info
        String pdfPath = "Property_" + savedProperty.getId() + ".pdf";

        try {
            PdfWriter writer = new PdfWriter(pdfPath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("🏨 Property Details"));
            document.add(new Paragraph("Name: " + savedProperty.getName()));
            document.add(new Paragraph("City: " + savedProperty.getCity().getName()));
            document.add(new Paragraph("State: " + savedProperty.getState().getName()));
            document.add(new Paragraph("Area: " + savedProperty.getArea().getName()));
            document.add(new Paragraph("Rooms: " + savedProperty.getNumberOfRooms()));
            document.add(new Paragraph("Beds: " + savedProperty.getNumberOfBeds()));
            document.add(new Paragraph("Bathrooms: " + savedProperty.getNumberOfBathrooms()));
            document.add(new Paragraph("✔️ Successfully Added"));

            document.close();
            System.out.println("✅ PDF Created at: " + pdfPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
         //this above code is to generate pdf
        // Send email via Kafka
        EmailRequest request = new EmailRequest(
                "pcsekhar2003@gmail.com",
                "Property added!",
                "Your property has been successfully added.",   pdfPath  // ✅ path of the generated PDF file

        );
        kafkaTemplate.send(AppConstants.TOPIC, request);

        // Upload photos to S3
        List<String> fileUrls = s3Service.uploadFiles(files);
        for (String url : fileUrls) {
            PropertyPhotos photo = new PropertyPhotos();
            photo.setUrl(url);
            photo.setProperty(savedProperty);
            propertyPhotosRepository.save(photo);
        }
        //to get api response with image urls
        dto.setImageUrls(fileUrls);

        // Return a DTO
        dto.setId(savedProperty.getId());
        return dto;
    }
 // ✅ 2. Search Property by name and date
    public APIResponse<List<Property>> searchProperty(String name, LocalDate date) {
        List<Property> properties = propertyRepository.searchProperty(name, date);

        APIResponse<List<Property>> response = new APIResponse<>();
        response.setStatus(200);
        response.setMessage("Search results found");
        response.setData(properties);

        return response;
    }
    public APIResponse<PropertyDto> findPropertyById(long id){
		APIResponse<PropertyDto> response = new APIResponse<>();
		PropertyDto dto  = new PropertyDto();
		Optional<Property> opProp = propertyRepository.findById(id);
		if(opProp.isPresent()) {
			Property property = opProp.get();
			dto.setArea(property.getArea().getName());
			dto.setCity(property.getCity().getName());
			dto.setState(property.getState().getName());
			List<Rooms> rooms = property.getRooms();
			List<RoomsDto> roomsDto = new ArrayList<>();
			for(Rooms room:rooms) {
				RoomsDto roomDto = new RoomsDto();
				BeanUtils.copyProperties(room, roomDto);
				roomsDto.add(roomDto);
			}
			dto.setRooms(roomsDto);
			BeanUtils.copyProperties(property, dto);
			response.setMessage("Matching Record");
			response.setStatus(200);
			response.setData(dto);
			return response;
		}
		
		return null;
	}

	public List<RoomAvailability> getTotalRoomsAvailable(long id) {
		return availabilityRepository.findByRoomId(id);
		
	}
	
	public Rooms getRoomById(long id) {
		return roomRepository.findById(id).get();
	}

}

