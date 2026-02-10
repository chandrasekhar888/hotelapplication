//BELOW COMENTS THERE IS UPDATED CODE FOR PDF SENDER
//package com.notificationservice.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.notificationservice.constants.AppConstants;
//import com.notificationservice.dto.EmailRequest;
//
//@Service
//public class EmailRequestListner {
//	
//	@Autowired
//	private JavaMailSender javamailsender ;
//	
//	@KafkaListener(topics = AppConstants.TOPIC, groupId="group_email")
//	public void kafakSubscriberContent(String emailRequest) {
//	    ObjectMapper mapper = new ObjectMapper();//You're doing deserialization in this line inside your EmailRequestListener:
//
//
//	    try {
//	        EmailRequest emailcontent = mapper.readValue(emailRequest, EmailRequest.class);
//	        SimpleMailMessage sm = new SimpleMailMessage();
//
//	        // Validate the recipient email before setting
//	        String toEmail = emailcontent.getTo();
//	        if (toEmail == null || toEmail.trim().isEmpty()) {
//	            System.err.println("Invalid recipient email: " + toEmail);
//	            return;
//	        }
//	        sm.setTo(toEmail.trim());
//
//	        sm.setSubject(emailcontent.getSubject());
//	        sm.setText(emailcontent.getBody());
//
//	        javamailsender.send(sm);
//
//	    } catch (JsonMappingException e) {
//	        e.printStackTrace();
//	    } catch (JsonProcessingException e) {
//	        e.printStackTrace();
//	    }
//	}
//}


//MODIFIED FOR PDFPATH

package com.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notificationservice.constants.AppConstants;
import com.notificationservice.dto.EmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailRequestListner {

    @Autowired
    private JavaMailSender javaMailSender;

    @KafkaListener(topics = AppConstants.TOPIC, groupId = "group_email")
    public void kafkaSubscriberContent(String emailRequestJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            EmailRequest emailContent = mapper.readValue(emailRequestJson, EmailRequest.class);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = multipart

            helper.setTo(emailContent.getTo());
            helper.setSubject(emailContent.getSubject());
            helper.setText(emailContent.getBody());

            // Attach PDF if path is provided
            if (emailContent.getPdfPath() != null && !emailContent.getPdfPath().isBlank()) {
                File pdf = new File(emailContent.getPdfPath());
                if (pdf.exists()) {
                    helper.addAttachment("Property_Details.pdf", pdf);
                } else {
                    System.err.println("❌ PDF not found at: " + emailContent.getPdfPath());
                }
            }

            javaMailSender.send(message);
            System.out.println("✅ Email sent to: " + emailContent.getTo());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to send email.");
        }
    }
}















