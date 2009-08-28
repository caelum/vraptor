package br.com.caelum.vraptor.example.spring;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class DogsRepository {

	public List<String> getDogs() {
		return Arrays.asList("lulu", "pluto");
	}
}
