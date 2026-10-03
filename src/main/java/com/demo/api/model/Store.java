package com.demo.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * A store. The id is assigned automatically when it is created.
 */
public record Store(
		Long id,
		@NotBlank @Size(min = 3, max = 30) String name,
		@NotBlank @Size(min = 3, max = 300) String address) {

	public Store withId(Long newId) {
		return new Store(newId, name, address);
	}
}
