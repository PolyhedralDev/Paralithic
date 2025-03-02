package com.dfsek.paralithic.sampler.normalizer;

import com.dfsek.paralithic.Expression;
import com.dfsek.paralithic.eval.parser.Parser;
import com.dfsek.paralithic.eval.parser.Parser.ParseOptions;
import com.dfsek.paralithic.eval.parser.Scope;
import com.dfsek.paralithic.eval.tokenizer.ParseException;
import com.dfsek.paralithic.functions.Function;
import com.dfsek.seismic.algorithms.sampler.normalizer.Normalizer;
import com.dfsek.seismic.type.sampler.Sampler;

import java.util.Map;



public class ExpressionNormalizer extends Normalizer {

    private final Expression expression;

    public ExpressionNormalizer(Sampler sampler, Map<String, Function> functions, String eq, Map<String, Double> vars, ParseOptions parseOptions)
    throws ParseException {
        super(sampler);

        Parser p = new Parser(parseOptions);
        Scope scope = new Scope();

        // 'in' was used as the invocation variable but conflicts with
        // the new 'in' keyword in Paralithic used to denote the end of a let
        // expression. To maintain backwards compatibility but also enable the use
        // of let expressions, if they're enabled then use the longer 'input'
        // invocation variable instead.
        if (parseOptions.useLetExpressions()) {
            scope.addInvocationVariable("input");
        } else {
            scope.addInvocationVariable("in");
        }

        vars.forEach(scope::create);
        functions.forEach(p::registerFunction);
        expression = p.parse(eq, scope);
    }

    @Override
    public double normalize(double in) {
        return expression.evaluate(in);
    }
}
