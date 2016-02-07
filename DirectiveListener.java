public class DirectiveListener extends CalcBaseListener {

    @Override
    public void exitFunc(CalcParser.FuncContext ctx) {
        System.out.println("RETF\n");
    }

    @Override
    public void exitCall(CalcParser.CallContext ctx) {
        System.out.println("CALL");
    }

    @Override
    public void exitPrintExpr(CalcParser.PrintExprContext ctx) {
        System.out.println("RET\n");
    }

    @Override
    public void exitAssign(CalcParser.AssignContext ctx) {
        String id = ctx.ID().getText();
        System.out.println("STR " + id);
    }

    @Override
    public void exitModMulDiv(CalcParser.ModMulDivContext ctx) {
        if (ctx.op.getType() == CalcParser.MOD) {
            System.out.println("MOD");
        } else if (ctx.op.getType() == CalcParser.MUL) {
            System.out.println("MUL");
        } else {
            System.out.println("DIV");
        }
    }

    @Override
    public void exitAddSub(CalcParser.AddSubContext ctx) {
        if (ctx.op.getType() == CalcParser.ADD) {
            System.out.println("ADD");
        } else {
            System.out.println("SUB");
        }
    }

    @Override
    public void exitId(CalcParser.IdContext ctx) {
        System.out.println("LDV " + ctx.ID().getText());
    }

    @Override
    public void exitNum(CalcParser.NumContext ctx) {
        System.out.println("LDC " + ctx.NUM().getText());
    }

    @Override
    public void exitUnary(CalcParser.UnaryContext ctx) {
        if ("-".equals(ctx.sign.getText())) System.out.println("NEG");
    }
}
