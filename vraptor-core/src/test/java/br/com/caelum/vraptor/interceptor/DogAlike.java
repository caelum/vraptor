package br.com.caelum.vraptor.interceptor;

import java.util.List;

public interface DogAlike {
    abstract void bark();
    abstract void bark(int times);
    abstract void bark(String phrase);
    abstract void eat(List<String> portions);
}
