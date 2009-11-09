package br.com.caelum.vraptor.rest;

import java.util.List;

public interface StateControl {

	public String getStateField();

	public List<Transition> getTransitions();

}
