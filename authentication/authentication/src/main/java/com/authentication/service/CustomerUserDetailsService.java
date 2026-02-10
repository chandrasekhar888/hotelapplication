//---------------------------------
package com.authentication.service;

import java.util.Collections;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.authentication.repository.UserRepository;

@Service
public class CustomerUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;

	@Override
public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException {

    com.authentication.entity.User user =
            userRepository.findByUsername(username);

    if (user == null) {
        throw new UsernameNotFoundException("User not found: " + username);
    }

   /* return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.emptyList())
            .build();
           This is commented because we are using role based */
           		return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),Collections.singleton(new SimpleGrantedAuthority(user.getRole())));//This should be modified

}


}
//---------------------------------