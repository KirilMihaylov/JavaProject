package me.web_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.web_server.service.IUserService;

@RestController
@RequestMapping("/api")
public class RestApiController {
	@Autowired
	private IUserService userService;

	@GetMapping("/auth_admin")
	public boolean authAdmin(@RequestParam("user") String username, @RequestParam("pass") byte[] passwordHash) {
		return userService.authenticateAdmin(username, passwordHash);
	}

	@GetMapping("/auth_seller")
	public boolean authSeller(@RequestParam("user") String username, @RequestParam("pass") byte[] passwordHash) {
		return userService.authenticateSeller(username, passwordHash);
	}
}
