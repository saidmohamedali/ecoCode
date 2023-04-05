package fr.greencodeinitiative.java.checks;

import org.sonar.java.model.expression.NewArrayTreeImpl;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CookieWithoutExpirationRule extends IssuableSubscriptionVisitor {

    private static final String COOKIE_CLASS_NAME = "javax.servlet.http.Cookie";
    private static final String SET_MAX_AGE_METHOD_NAME = "setMaxAge";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.CLASS);
    }

    @Override
    public void visitNode(Tree tree) {
        ClassTree classTree = (ClassTree) tree;

        for (Tree member : classTree.members()) {
            if (member.is(Tree.Kind.METHOD)) {
                MethodInvocationTree methodInvocation = getMethodInvocation((MethodTree) member);
                if (methodInvocation != null && isCookieConstructor(methodInvocation)) {
                    checkCookieExpiration(methodInvocation);
                }
            }
        }
    }

    private MethodInvocationTree getMethodInvocation(MethodTree tree) {
        List<Tree> children = tree.getChildren();
        for (Tree child : children) {
            if (child instanceof MethodInvocationTree) {
                return (MethodInvocationTree) child;
            }
        }
        return null;
    }

    private boolean isCookieConstructor(MethodInvocationTree methodInvocation) {
        ExpressionTree methodSelect = methodInvocation.methodSelect();
        if (methodSelect.is(Tree.Kind.IDENTIFIER)) {
            IdentifierTree identifier = (IdentifierTree) methodSelect;
            return identifier.name().equals("Cookie");
        }
        return false;
    }

    private void checkCookieExpiration(MethodInvocationTree methodInvocation) {
        List<ExpressionTree> arguments = methodInvocation.arguments();
        for (ExpressionTree argument : arguments) {
            if (argument.symbolType().fullyQualifiedName().equals(COOKIE_CLASS_NAME)) {
                checkCookieExpirationArgument(argument);
            }
        }
    }

    private void checkCookieExpirationArgument(ExpressionTree argument) {
        boolean hasExpiration = false;
/*
        if (argument.is(Tree.Kind.NEW_ARRAY)) {
            ExpressionTree[] dimensions = ((NewArrayTreeImpl) argument).dimensions();
            for (ExpressionTree dimension : dimensions) {
                if (dimension.toString().equals("1")) {
                    hasExpiration = true;
                }
            }
        } else if (argument.is(Tree.Kind.NEW_CLASS)) {
            List<ExpressionTree> arguments = ((NewClassTreeImpl) argument).arguments();
            for (ExpressionTree arg : arguments) {
                if (arg.symbolType().fullyQualifiedName().equals("java.lang.Integer")) {
                    MethodInvocationTree invocationTree = (MethodInvocationTree) arg;
                    if (invocationTree.symbol().name().equals("valueOf") && invocationTree.arguments().get(0).toString().equals("-1")) {
                        hasExpiration = true;
                    }
                }
            }
        }
*/
        if (!hasExpiration) {
            reportIssue(argument, "Le cookie est créé sans date d'expiration.");
        }
    }
}
