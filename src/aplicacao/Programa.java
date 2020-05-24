package aplicacao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import bancoDeDados.BancoDeDados;
import bancoDeDados.excexoes.BancoDeDadosException;
import bancoDeDados.excexoes.BancoDeDadosIntegridadeException;

public class Programa {

	public static void main(String[] args) {

		Connection conexao = null;
		Statement declaracao = null;
		Statement declaracaoTransacao = null;
		PreparedStatement declaracaoInsercao = null;
		PreparedStatement declaracaoAtualizacao = null;
		PreparedStatement declaracaoDelecao = null;
		ResultSet resultado = null;
		SimpleDateFormat formatacaoDeData = new SimpleDateFormat("dd/MM/yyy");

		try {
			conexao = BancoDeDados.getConexao();
			declaracao = conexao.createStatement();
			resultado = declaracao.executeQuery("SELECT * FROM departamento");
			while (resultado.next()) {
				System.out.println(resultado.getInt("Id") + ";" + resultado.getString("Nome"));
			}

			declaracaoInsercao = conexao.prepareStatement("INSERT INTO vendedor(Nome,Email,DataNascimento,SalarioBase,IdDepartamento) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			declaracaoInsercao.setString(1, "Carl Purple");
			declaracaoInsercao.setString(2, "carl@gmail.com");
			declaracaoInsercao.setDate(3, new Date(formatacaoDeData.parse("22/04/1995").getTime()));
			declaracaoInsercao.setDouble(4, 3000.0);
			declaracaoInsercao.setInt(5, 4);

			Integer linhasIncluidas = declaracaoInsercao.executeUpdate();

			if (linhasIncluidas > 0) {
				ResultSet resultadoInsercao = declaracaoInsercao.getGeneratedKeys();
				System.out.println("Inclusão concluída! Linhas incluídas: " + linhasIncluidas);
				while (resultadoInsercao.next()) {
					int id = resultadoInsercao.getInt(1);
					System.out.println("Id inserido:" + id);
				}
			}

			declaracaoAtualizacao = conexao.prepareStatement("UPDATE vendedor SET SalarioBase = SalarioBase + ? WHERE (IdDepartamento = ?)");
			declaracaoAtualizacao.setDouble(1, 200.0);
			declaracaoAtualizacao.setInt(2, 4);

			Integer linhasAtualizadas = declaracaoAtualizacao.executeUpdate();
			System.out.println("Concluido! Linhas atualizadas: " + linhasAtualizadas);

			declaracaoDelecao = conexao.prepareStatement("DELETE FROM departamento WHERE Id = ?");
			declaracaoDelecao.setInt(1, 2);

			Integer linhasDeletadas = declaracaoDelecao.executeUpdate();
			System.out.println("Concluido! Linhas atualizadas: " + linhasDeletadas);

			conexao.setAutoCommit(false);
			declaracaoTransacao = conexao.createStatement();

			Integer atualizacao1 = declaracaoTransacao.executeUpdate("UPDATE vendedor SET SalarioBase = 2090 WHERE IdDepartamento = 1");

//			if (true) {
//				throw new SQLException("Simulacao de erro");
//			}

			Integer atualizacao2 = declaracaoTransacao.executeUpdate("UPDATE vendedor SET SalarioBase = 3090 WHERE IdDepartamento = 2");
			conexao.commit();

			System.out.println("Atualizacao 1: " + atualizacao1 + " linhas atualizadas");
			System.out.println("Atualizacao 2: " + atualizacao2 + " linhas atualizadas");

		} catch (MySQLIntegrityConstraintViolationException e) {
			throw new BancoDeDadosIntegridadeException(e.getMessage());
		} catch (SQLException | ParseException e) {
			try {
				conexao.rollback();
				throw new BancoDeDadosException("Transação revertida! Causa: " + e.getMessage());
			} catch (SQLException e1) {
				throw new BancoDeDadosException("Erro na reversão da transação!");
			}
		} finally {
			BancoDeDados.fecharDeclaracao(declaracao);
			BancoDeDados.fecharResultado(resultado);
			BancoDeDados.fecharConexao();
		}

	}

}
