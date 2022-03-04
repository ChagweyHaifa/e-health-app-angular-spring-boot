package com.backend.ehealthspringboot.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.backend.ehealthspringboot.exception.ExceptionHandling;
import com.backend.ehealthspringboot.exception.domain.UsernameExistException;



@RestController
@RequestMapping(path= {"/","/user"})
// it will look for a handler for this exception in ExceptionHandling class
public class UserResource extends ExceptionHandling {
	@GetMapping("home")
	public String showUser(){
		return "home page";
	}
	@GetMapping("login") 
	public String showlogin() throws UsernameExistException  {
		
		throw new UsernameExistException("to ken has been expired");

	}
}
