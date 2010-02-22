package br.com.caelum.vraptor.blank;

public class Produto {

	private String nome;
	private double preco;
	
	public Produto() {
		this("",0);
	}
	
	public Produto(String nome,double preco) {
		this.nome = nome;
		this.preco = preco;
		
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public double getPreco() {
		return preco;
	}

	public void setPreco(double preco) {
		this.preco = preco;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return nome+";"+preco;
	}
}
