package br.com.caelum.vraptor.proxy;

/**
 * @author Fabio Kung
 */
public class TheClassWithComplexConstructor {
    private final TheClass firstDependency;
    private final TheInterface secondDependency;

    public TheClassWithComplexConstructor(TheClass firstDependency, TheInterface secondDependency) {
        this.firstDependency = firstDependency;
        this.secondDependency = secondDependency;
    }

    public TheClass getFirstDependency() {
        return firstDependency;
    }

    public TheInterface getSecondDependency() {
        return secondDependency;
    }
}
