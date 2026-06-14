package highlighting.antlr;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

public final class PrettyPrinterVisitor extends MiniJavaBaseVisitor<Void> {
    private final StringBuilder out = new StringBuilder();
    private final int indentWidth;
    private int currentIndent = 0;
    private boolean atLineStart = true;

    private Token lastToken = null;

    public PrettyPrinterVisitor(int indentWidth) {
        this.indentWidth = Math.max(0, indentWidth);
    }

    public String result() {
        return out.toString();
    }

    @Override
    public Void visitCompilationUnit(MiniJavaParser.CompilationUnitContext ctx) {
        if (ctx.packageDecl() != null) {
            visit(ctx.packageDecl());
            nl();

            if (!ctx.importDecl().isEmpty() || !ctx.typeDecl().isEmpty()) {
                nl();
            }
        }

        for (int i = 0; i < ctx.importDecl().size(); i++) {
            visit(ctx.importDecl(i));
            nl();
        }

        if (!ctx.importDecl().isEmpty() && !ctx.typeDecl().isEmpty()) {
            nl();
        }

        for (int i = 0; i < ctx.typeDecl().size(); i++) {
            visit(ctx.typeDecl(i));

            if (i + 1 < ctx.typeDecl().size()) {
                nl();
                nl();
            }
        }

        return null;
    }

    @Override
    public Void visitClassBody(MiniJavaParser.ClassBodyContext ctx) {
        writeOpeningBrace();
        nl();

        currentIndent++;

        for (MiniJavaParser.ClassBodyDeclarationContext declaration : ctx.classBodyDeclaration()) {
            indent();
            visit(declaration);

            if (!atLineStart) {
                nl();
            }
        }

        currentIndent--;

        write("}");

        return null;
    }

    @Override
    public Void visitBlock(MiniJavaParser.BlockContext ctx) {
        writeOpeningBrace();
        nl();

        currentIndent++;

        for (MiniJavaParser.BlockStatementContext statement : ctx.blockStatement()) {
            indent();
            visit(statement);

            if (!atLineStart) {
                nl();
            }
        }

        currentIndent--;

        write("}");

        return null;
    }

    @Override
    public Void visitStatement(MiniJavaParser.StatementContext ctx) {
        int firstToken = ctx.getStart().getType();

        if (firstToken == MiniJavaLexer.LBRACE) {
            visit(ctx.getChild(0));
            return null;
        }

        if (firstToken == MiniJavaLexer.IF) {
            write("if (");
            visit(ctx.getChild(2));
            write(")");

            MiniJavaParser.StatementContext thenStatement =
                (MiniJavaParser.StatementContext) ctx.getChild(4);

            visitControlledStatement(thenStatement);

            if (ctx.getChildCount() > 5) {
                MiniJavaParser.StatementContext elseStatement =
                    (MiniJavaParser.StatementContext) ctx.getChild(6);

                if (isBlockStatement(thenStatement)) {
                    write(" else");
                } else {
                    if (!atLineStart) {
                        nl();
                    }
                    write("else");
                }

                visitControlledStatement(elseStatement);
            }

            return null;
        }

        if (firstToken == MiniJavaLexer.WHILE) {
            write("while (");
            visit(ctx.getChild(2));
            write(")");

            MiniJavaParser.StatementContext body =
                (MiniJavaParser.StatementContext) ctx.getChild(4);

            visitControlledStatement(body);

            return null;
        }

        visitChildren(ctx);

        if (!atLineStart) {
            nl();
        }

        return null;
    }

    private void visitControlledStatement(MiniJavaParser.StatementContext statement) {
        if (isBlockStatement(statement)) {
            visit(statement);
        } else {
            nl();
            currentIndent++;
            indent();
            visit(statement);
            currentIndent--;
        }
    }

    private boolean isBlockStatement(MiniJavaParser.StatementContext statement) {
        return statement.getStart().getType() == MiniJavaLexer.LBRACE;
    }

    private void writeOpeningBrace() {
        if (atLineStart) {
            write("{");
        } else {
            write(" {");
        }
    }

    private void indent() {
        if (atLineStart) {
            out.repeat(" ", Math.max(0, indentWidth * currentIndent));
            atLineStart = false;
        }
    }

    private void write(String s) {
        if (s == null || s.isEmpty()) return;
        indent();
        out.append(s);
    }

    private void nl() {
        out.append('\n');
        atLineStart = true;
        lastToken = null;
    }

    private void writeln(String s) {
        write(s);
        nl();
    }

    @Override
    public Void visitTerminal(TerminalNode node) {
        Token t = node.getSymbol();
        String text = t.getText();

        if (lastToken != null) {
            int prevType = lastToken.getType();
            int curType = t.getType();

            if (needsSpaceBetween(prevType, curType)) {
                write(" ");
            }
        }

        write(text);
        lastToken = t;

        return null;
    }

    private boolean needsSpaceBetween(int prevType, int curType) {
        return isWordLike(prevType) && isWordLike(curType);
    }

    private boolean isWordLike(int type) {
        return type == MiniJavaLexer.IDENTIFIER
            || type == MiniJavaLexer.STRING_LITERAL
            || type == MiniJavaLexer.CHAR_LITERAL
            || type == MiniJavaLexer.NULL
            || type == MiniJavaLexer.PACKAGE
            || type == MiniJavaLexer.IMPORT
            || type == MiniJavaLexer.CLASS
            || type == MiniJavaLexer.PUBLIC
            || type == MiniJavaLexer.PRIVATE
            || type == MiniJavaLexer.FINAL
            || type == MiniJavaLexer.RETURN
            || type == MiniJavaLexer.NEW
            || type == MiniJavaLexer.IF
            || type == MiniJavaLexer.ELSE
            || type == MiniJavaLexer.WHILE
            || type == MiniJavaLexer.EXTENDS
            || type == MiniJavaLexer.IMPLEMENTS;
    }
}
