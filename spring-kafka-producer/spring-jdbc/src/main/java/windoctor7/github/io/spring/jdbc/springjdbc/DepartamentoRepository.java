package windoctor7.github.io.spring.jdbc.springjdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 15/10/17.
 */
@Repository
public class DepartamentoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Departamento> findAll(){
        return jdbcTemplate.query("select * from departamento",
                new BeanPropertyRowMapper<>(Departamento.class));
    }
}
