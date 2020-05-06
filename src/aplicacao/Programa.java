package aplicacao;

import java.sql.Connection;

import bancoDeDados.BancoDeDados;

public class Programa {

	public static void main(String[] args) {

		Connection conexao = BancoDeDados.getConexao();
		BancoDeDados.fecharConexao();

	}

}
