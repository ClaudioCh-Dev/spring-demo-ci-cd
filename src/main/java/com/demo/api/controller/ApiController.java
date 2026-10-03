package com.demo.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.demo.api.model.Product;
import com.demo.api.model.Store;
import com.demo.api.repository.StoreRepository;

import jakarta.validation.Valid;

@RestController
public class ApiController {

	private final StoreRepository repository;
	private final String version;

	public ApiController(StoreRepository repository, @Value("${app.version}") String version) {
		this.repository = repository;
		this.version = version;
	}

	@GetMapping("/")
	public String root() {
		return "ok";
	}

	@GetMapping("/status")
	public Map<String, String> status() {
		return Map.of("status", "healthy", "version", version);
	}

	// Stores
	@GetMapping("/stores")
	public List<Store> getStores() {
		return repository.findAll();
	}

	@GetMapping("/stores/{id}")
	public Store getStore(@PathVariable long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));
	}

	@PostMapping("/stores")
	@ResponseStatus(HttpStatus.CREATED)
	public Store addStore(@Valid @RequestBody Store store) {
		return repository.save(store);
	}

	@DeleteMapping("/stores/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteStore(@PathVariable long id) {
		if (!repository.deleteById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found");
		}
	}

	// Products
	@GetMapping("/products")
	public List<Product> getProducts() {
		return repository.findAllProducts();
	}
}
