package br.com.aguaemdia.agua_em_dia_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Confere que a aplicação inicia, conecta no MySQL e que o Flyway criou todas as tabelas. */
@SpringBootTest
class AguaEmDiaApiApplicationTests {

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void contextLoads() {
	}

	@Test
	void conectaNoMySql() {
		Integer resultado = jdbc.queryForObject("select 1", Integer.class);
		assertEquals(1, resultado);
	}

	@Test
	void tabelasForamCriadasPelasMigrations() {
		Integer tabelas = jdbc.queryForObject(
				"select count(*) from information_schema.tables where table_schema = database() "
						+ "and table_name in ('usuario','vazamento','apoio','avaliacao','recuperacao_senha')",
				Integer.class);
		assertEquals(5, tabelas);
	}
}
