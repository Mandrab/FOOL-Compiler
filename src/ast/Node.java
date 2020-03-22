package ast;

import lib.*;

public interface Node extends Visitable {

	String codeGeneration();

}