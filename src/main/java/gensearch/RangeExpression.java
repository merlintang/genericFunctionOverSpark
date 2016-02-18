package gensearch;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.tokenizer.NumberToken;
import net.objecthunter.exp4j.tokenizer.OperatorToken;
import net.objecthunter.exp4j.tokenizer.Token;
import net.objecthunter.exp4j.tokenizer.VariableToken;

public class RangeExpression {

	private final Token[] tokens;

    private final Map<String, Range> variables;
	
	public RangeExpression(Expression exp){
		this.tokens = exp.getTokens();
		this.variables = new HashMap<String, Range>(4);
	}
	
	public RangeExpression setVariable(final String name, final Range range) {
        this.variables.put(name, range);
        return this;
    }
	
	public Range evaluate() {
        final Stack<Range> output = new Stack<Range>();
        for (int i = 0; i < tokens.length; i++) {
            Token t = tokens[i];
            if (t.getType() == Token.TOKEN_NUMBER) {
                output.push( new Range( ((NumberToken) t).getValue(),((NumberToken) t).getValue() ) );
            } else if (t.getType() == Token.TOKEN_VARIABLE) {
                final String name = ((VariableToken) t).getName();
                final Range rvalue = this.variables.get(name);
                if (rvalue == null) {
                    throw new IllegalArgumentException("No value has been set for the setVariable '" + name + "'.");
                }
                output.push(rvalue);
            } else if (t.getType() == Token.TOKEN_OPERATOR) {
                OperatorToken op = (OperatorToken) t;
                if (output.size() < op.getOperator().getNumOperands()) {
                    throw new IllegalArgumentException("Invalid number of operands available for '" + op.getOperator().getSymbol() + "' operator");
                }
                if (op.getOperator().getNumOperands() == 2) {
                    /* pop the operands and push the result of the operation */
                    Range rightArg = output.pop();
                    Range leftArg = output.pop();
                    RangeOperation rop = new RangeOperation(op.getOperator());
                    output.push( rop.applyRangeOperation(leftArg, rightArg));
                } else if (op.getOperator().getNumOperands() == 1) {
                    /* pop the operand and push the result of the operation */
                	Range arg = output.pop();
                	RangeOperation rop = new RangeOperation(op.getOperator());
                    output.push( rop.applyRangeOperation(arg));
                }
            } else if (t.getType() == Token.TOKEN_FUNCTION) {
            	throw new IllegalArgumentException("We don't support functions yet!!!");
            	/*
                FunctionToken func = (FunctionToken) t;
                if (output.size() < func.getFunction().getNumArguments()) {
                    throw new IllegalArgumentException("Invalid number of arguments available for '" + func.getFunction().getName() + "' function");
                }
                // collect the arguments from the stack 
                double[] args = new double[func.getFunction().getNumArguments()];
                for (int j = 0; j < func.getFunction().getNumArguments(); j++) {
                    args[j] = output.pop();
                }
                output.push(func.getFunction().apply(this.reverseInPlace(args)));
                */
            }
        }
        if (output.size() > 1) {
            throw new IllegalArgumentException("Invalid number of items on the output queue. Might be caused by an invalid number of arguments for a function.");
        }
        return output.pop();
    }

	public static void main(String[] args) {
		//exp = new expbuilder("x1*x2").vars(x1,x2)
		//rangeExpW = new RangeExpW(exp)
		//Range r = rangeExpW.setVarRange(x1, new Range(1,2) ).setVarRange(x1, new Range( 2, 3 ) ). eval()
		//bool res = r.hasChangedSigned()
		
		Expression exp = new ExpressionBuilder("x1/x2").variables("x1","x2","x3").build();
		RangeExpression rexp = new RangeExpression(exp);
		rexp.setVariable("x1", new Range(1,2,-5,6,-1,-3))
			.setVariable("x2", new Range(-3,6,1,2))
			.setVariable("x3", new Range(-3,6,1,2));
		System.out.println(rexp.evaluate());
	}

}
