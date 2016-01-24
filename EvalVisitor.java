import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;

public class EvalVisitor extends CalcBaseVisitor<Integer> {
    /**
     * Functions definitions and their start in the tokenstream (for pushing them in nested calls).
     * It's important to keep the order of the definitions, therefore we use a LinkedHashMap here.
     */
    private Map<CalcParser.FuncContext, Integer> functionDefinitions = new LinkedHashMap<CalcParser.FuncContext, Integer>();

    /** Remember local variables. Currently, this is only the function parameter. */
    private Map<String, Integer> localMemory = new HashMap<String, Integer>();

    /** Remember global variables set by =. */
    private Map<String, Integer> globalMemory = new HashMap<String, Integer>();

    /**
     * Find matching function definition for a function name and parameter
     * value. The first definition is returned where (a) the name matches
     * and (b) the formal parameter agrees if it is defined as constant.
     */
    private CalcParser.FuncContext findFunction(String name, Integer paramValue) {
        for (CalcParser.FuncContext fn : functionDefinitions.keySet()) {
            if (fn.ID(0).getText().equals(name)) {
                // Check whether parameter matches
                if (fn.parm.getType() == CalcParser.INT
                    && !new Integer(fn.parm.getText()).equals(paramValue)) {
                        // Constant in formalPar list does not match actual value -> no match.
                        continue;
                }
                // Parameter (value for INT formal arg) as well as fct name agrees!
                return fn;
            }
        }
        return null;
    }

    /** Get value of name up call stack. */
    public Integer getValue(String name) {
        Integer value = localMemory.get(name);
        if (value != null ) {
            return value;
        }
        value = globalMemory.get(name);
        if (value != null ) {
            return value;
        }
        // not found in local memory or global memory
        System.err.println("undefined variable " + name);
        return 0;
    }

    /** ID '(' expr ')' */
    @Override
    public Integer visitCall(CalcParser.CallContext ctx) {
        String name = ctx.ID().getText();
        Integer actualValue = visit(ctx.expr());

        Integer value = 0;
        CalcParser.FuncContext fn = findFunction(name, actualValue);
        if (fn == null) {
            System.err.println("No match found for " + name + "(" + actualValue + ")");
        } else {
            // Push parameter value into local memory
            String paramName = fn.parm.getText();
            Integer prevValue = localMemory.put(paramName, actualValue);

            value = visit(fn.expr());

            // Restore local variable to previous values.
            localMemory.put(paramName, prevValue);
        }

        return value;
    }

    /** ID '(' parm ')' '=' expr */
    @Override
    public Integer visitFunc(CalcParser.FuncContext ctx) {
        functionDefinitions.put(ctx, 0);
        return 0;
    }

    /** ID '=' expr */
    @Override
    public Integer visitAssign(CalcParser.AssignContext ctx) {
        String id = ctx.ID().getText();  // id is left-hand side of '='
        int value = visit(ctx.expr());   // compute value of expression on right
        globalMemory.put(id, value);     // store it in global memory
        return value;
    }

    /** expr */
    @Override
    public Integer visitPrintExpr(CalcParser.PrintExprContext ctx) {
        Integer value = visit(ctx.expr()); // evaluate the expr child
        System.out.println(value);         // print the result
        return 0;                          // return dummy value
    }

    /** INT */
    @Override
    public Integer visitInt(CalcParser.IntContext ctx) {
        return Integer.valueOf(ctx.INT().getText());
    }

    /** ID */
    @Override
    public Integer visitId(CalcParser.IdContext ctx) {
        return getValue(ctx.ID().getText());
    }

    /** expr op=('%'|'*'|'/') expr */
    @Override
    public Integer visitModMulDiv(CalcParser.ModMulDivContext ctx) {
        int left = visit(ctx.expr(0));  // get value of left subexpression
        int right = visit(ctx.expr(1)); // get value of right subexpression
        if (ctx.op.getType() == CalcParser.MOD) return left % right;
        if (ctx.op.getType() == CalcParser.MUL) return left * right;
        return left / right; // must be DIV
    }

    /** expr op=('+'|'-') expr */
    @Override
    public Integer visitAddSub(CalcParser.AddSubContext ctx) {
        int left = visit(ctx.expr(0));  // get value of left subexpression
        int right = visit(ctx.expr(1)); // get value of right subexpression
        if ( ctx.op.getType() == CalcParser.ADD ) return left + right;
        return left - right; // must be SUB
    }

    /** '(' expr ')' */
    @Override
    public Integer visitParens(CalcParser.ParensContext ctx) {
        return visit(ctx.expr()); // return child expr's value
    }
}
