package com.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authentication.dto.APIResponse;
import com.authentication.dto.LoginDto;
import com.authentication.dto.UserDto;
import com.authentication.service.AuthService;
import com.authentication.service.JwtService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	@Autowired
	private AuthService authService;
	@Autowired
	private AuthenticationManager authManager;

	@Autowired
private JwtService jwtService; //Added for JWT token

	@PostMapping("/register")
	public ResponseEntity<APIResponse<String>> register(@RequestBody UserDto dto) {
		APIResponse<String> response = authService.register(dto);//controller receives response from service
		return ResponseEntity.status(response.getStatus()).body(response);
	}
	/*@PostMapping("/login")
	 public ResponseEntity<APIResponse<String>> loginCheck(@RequestBody LoginDto loginDto)//this Dto has the username and password 
	 {
		 
		 APIResponse<String> response = new APIResponse<>();
		 
		 UsernamePasswordAuthenticationToken token = 
				 new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
		 
		try {
			 org.springframework.security.core.Authentication authenticate = authManager.authenticate(token);//Manager gives to DAO provider
			 
			 if(authenticate.isAuthenticated()) {
				 response.setMessage("Login Sucessful");
				 response.setStatus(200);
				 response.setData("User has logged");
				 return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		 response.setMessage("Failed");
		 response.setStatus(401);
		 response.setData("Un-Authorized Access");
		 return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
	 }*/

		 @PostMapping("/login")
public ResponseEntity<APIResponse<String>> loginCheck(@RequestBody LoginDto loginDto) {

    APIResponse<String> response = new APIResponse<>();

    UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(),
                    loginDto.getPassword()
            );

    try {
        // 🔐 Authenticate user
        org.springframework.security.core.Authentication authenticate =
                authManager.authenticate(token);

        // ✅ Authentication succeeded → no exception thrown
        org.springframework.security.core.userdetails.User userDetails =
                (org.springframework.security.core.userdetails.User)
                        authenticate.getPrincipal();

        // Extract role
        String role = userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        // ✅ Generate JWT
        String jwt = jwtService.generateToken(
                userDetails.getUsername(),
                role
        );

        response.setMessage("Login Successful");
        response.setStatus(200);
        response.setData(jwt); // ✅ RETURN JWT

        return ResponseEntity.ok(response);

    }
    // ✅ Catch only authentication failures
    catch (org.springframework.security.core.AuthenticationException ex) {

        response.setMessage("Failed");
        response.setStatus(401);
        response.setData("Unauthorized Access");

        return ResponseEntity
                .status(HttpStatusCode.valueOf(401))
                .body(response);
    }
}



}
