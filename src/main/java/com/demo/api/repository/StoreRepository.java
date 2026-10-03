package com.demo.api.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import com.demo.api.model.Product;
import com.demo.api.model.Store;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * In-memory "database" loaded from the JSON files in src/main/resources/data.
 * Data resets every time the app restarts.
 */
@Repository
public class StoreRepository {

	private final List<Store> stores = new CopyOnWriteArrayList<>();
	private final List<Product> products;
	private final AtomicLong nextId;

	public StoreRepository(JsonMapper mapper) {
		List<Store> initial = read(mapper, "data/stores.json", new TypeReference<List<Store>>() {});
		initial.sort(Comparator.comparing(Store::id));
		stores.addAll(initial);
		products = read(mapper, "data/products.json", new TypeReference<List<Product>>() {});
		nextId = new AtomicLong(initial.isEmpty() ? 1 : initial.getLast().id() + 1);
	}

	public List<Store> findAll() {
		return List.copyOf(stores);
	}

	public Optional<Store> findById(long id) {
		return stores.stream().filter(s -> s.id() == id).findFirst();
	}

	public Store save(Store store) {
		Store created = store.withId(nextId.getAndIncrement());
		stores.add(created);
		return created;
	}

	public boolean deleteById(long id) {
		return stores.removeIf(s -> s.id() == id);
	}

	public List<Product> findAllProducts() {
		return products;
	}

	private static <T> T read(JsonMapper mapper, String path, TypeReference<T> type) {
		try (InputStream in = new ClassPathResource(path).getInputStream()) {
			return mapper.readValue(in, type);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot load " + path, e);
		}
	}
}
