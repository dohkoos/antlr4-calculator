import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;

public class EvalVisitor extends CalcBaseVisitor<Double> {
    /**
     * Functions definitions and their start in the tokenstream (for pushing them in nested calls).
     * It's important to keep the order of the definitions, therefore we use a LinkedHashMap here.
     */
    private Map<CalcParser.FuncContext, Double> functionDefinitions = new LinkedHashMap<CalcParser.FuncContext, Double>();

    /** Remember local variables. Currently, this is only the function parameter. */
    private Map<String, Double> localMemory = new HashMap<String, Double>();

    /** Remember global variables set by =. */
    private Map<String, Double> globalMemory = new HashMap<String, Double>();

    /**
     * Find matching function definition for a function name and parameter
     * value. The first definition is returned where (a) the name matches
     * and (b) the formal parameter agrees if it is defined as constant.
     */
    private CalcParser.FuncContext findFunction(String name, Double paramValue) {
        for (CalcParser.FuncContext fn : functionDefinitions.keySet()) {
            if (fn.ID(0).getText().equals(name)) {
                // Check whether parameter matches
                if (fn.parm.getType() == CalcParser.NUM
                    && !new Double(fn.parm.getText()).equals(paramValue)) {
                        // Constant in formalPar list does not match actual value -> no match.
                        continue;
                }
                // Parameter (value for NUM formal arg) as well as fct name agrees!
                return fn;
            }
        }
        return null;
    }

    /** Get value of name up call stack. */
    public Double getValue(String name) {
        Double value = localMemory.get(name);
        if (value != null ) {
            return value;
        }
        value = globalMemory.get(name);
        if (value != null ) {
            return value;
        }
        // not found in local memory or global memory
        System.err.println("undefined variable " + name);
        return 0d;
    }

    /** ID '(' expr ')' */
    @Override
    public Double visitCall(CalcParser.CallContext ctx) {
        String name = ctx.ID().getText();
        Double actualValue = visit(ctx.expr());

        Double value = 0d;
        CalcParser.FuncContext fn = findFunction(name, actualValue);
        if (fn == null) {
            System.err.println("No match found for " + name + "(" + actualValue + ")");
        } else {
            // Push parameter value into local memory
            String paramName = fn.parm.getText();
            Double prevValue = localMemory.put(paramName, actualValue);

            value = visit(fn.expr());

            // Restore local variable to previous values.
            localMemory.put(paramName, prevValue);
        }

        return value;
    }

    /** ID '(' parm ')' '=' expr */
    @Override
    public Double visitFunc(CalcParser.FuncContext ctx) {
        functionDefinitions.put(ctx, 0d);
        return 0d;
    }

    /** ID '=' expr */
    @Override
    public Double visitAssign(CalcParser.AssignContext ctx) {
        String id = ctx.ID().getText();  // id is left-hand side of '='
        double value = visit(ctx.expr());   // compute value of expression on right
        globalMemory.put(id, value);     // store it in global memory
        return value;
    }

    /** expr */
    @Override
    public Double visitPrintExpr(CalcParser.PrintExprContext ctx) {
        Double value = visit(ctx.expr()); // evaluate the expr child
        System.out.println(value);         // print the result
        return 0d;                          // return dummy value
    }

    /** NUM */
    @Override
    public Double visitNum(CalcParser.NumContext ctx) {
        return Double.valueOf(ctx.NUM().getText());
    }

    /** ('+'|'-') expr */
    @Override
    public Double visitUnary(CalcParser.UnaryContext ctx) {
        double value = visit(ctx.expr());
        if ("-".equals(ctx.sign.getText())) return value * -1;
        return value;
    }

    /** ID */
    @Override
    public Double visitId(CalcParser.IdContext ctx) {
        return getValue(ctx.ID().getText());
    }

    /** expr op=('%'|'*'|'/') expr */
    @Override
    public Double visitModMulDiv(CalcParser.ModMulDivContext ctx) {
        double left = visit(ctx.expr(0));  // get value of left subexpression
        double right = visit(ctx.expr(1)); // get value of right subexpression
        if (ctx.op.getType() == CalcParser.MOD) return left % right;
        if (ctx.op.getType() == CalcParser.MUL) return left * right;
        return left / right; // must be DIV
    }

    /** expr op=('+'|'-') expr */
    @Override
    public Double visitAddSub(CalcParser.AddSubContext ctx) {
        double left = visit(ctx.expr(0));  // get value of left subexpression
        double right = visit(ctx.expr(1)); // get value of right subexpression
        if ( ctx.op.getType() == CalcParser.ADD ) return left + right;
        return left - right; // must be SUB
    }

    /** '(' expr ')' */
    @Override
    public Double visitParens(CalcParser.ParensContext ctx) {
        return visit(ctx.expr()); // return child expr's value
    }
}
