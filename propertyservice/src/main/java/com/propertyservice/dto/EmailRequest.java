package com.propertyservice.dto;

public class EmailRequest {
	
    private String to;
    private String subject;
    private String body;
    private String pdfPath; // New field For Generation

    
    
	public EmailRequest(String to, String subject, String body, String pdfPath) {
		this.to = to;
		this.subject = subject;
		this.body = body;
		this.pdfPath=pdfPath;
	}
	public EmailRequest() {
		super();
	}
	
	public String getTo() {
		return to;
	}
	public String getSubject() {
		return subject;
	}
	public String getBody() {
		return body;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getPdfPath() {
	    return pdfPath;
	}

	public void setPdfPath(String pdfPath) {
	    this.pdfPath = pdfPath;
	}
	@Override
	public String toString() {
		return "EmailRequest [to=" + to + ", subject=" + subject + ", body=" + body + ", pdfPath=" + pdfPath + "]";
	}

    
}