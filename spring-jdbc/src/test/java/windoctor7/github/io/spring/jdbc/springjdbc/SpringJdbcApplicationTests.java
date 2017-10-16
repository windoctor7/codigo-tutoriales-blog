package windoctor7.github.io.spring.jdbc.springjdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("test") //establecemos a "test" el profile para iniciar esta prueba.
public class SpringJdbcApplicationTests {

	@Autowired
	DepartamentoRepository repository;

	@Test
	public void contarDepartamentos() {
		List<Departamento> departamentos = repository.findAll();
		assertNotNull(departamentos); // validamos que la lista no sea null
		assertEquals(5, departamentos.size()); // validamos que existan 5 departamentos
	}
}
