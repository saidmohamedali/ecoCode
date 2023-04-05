package fr.greencodeinitiative.java.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.MethodMatchers;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.NewClassTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Arrays;
import java.util.List;

@Rule(key = "EC1",
        name = "Developpement",
        description = Coookie2Check.RULE_MESSAGE,
        priority = Priority.MINOR,
        tags = {"bug"})
public class Coookie2Check extends IssuableSubscriptionVisitor {

    protected static final String RULE_MESSAGE = "Avoid Spring repository call in loop";

    private static final String SPRING_REPOSITORY = "Cookie";

    private static final MethodMatchers REPOSITORY_METHOD =
            MethodMatchers.create().ofSubTypes(SPRING_REPOSITORY).anyName().withAnyParameters()
                    .build();

    private final AvoidSpringRepositoryCallInLoopCheckVisitor visitorInFile = new AvoidSpringRepositoryCallInLoopCheckVisitor();

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(Tree.Kind.NEW_CLASS, Tree.Kind.METHOD_INVOCATION);

    }

    @Override
    public void visitNode(Tree tree) {
        tree.accept(visitorInFile);
    }

    private class AvoidSpringRepositoryCallInLoopCheckVisitor extends BaseTreeVisitor {
        @Override
        public void visitMethodInvocation(MethodInvocationTree tree) {
            if (REPOSITORY_METHOD.matches(tree)) {
                reportIssue(tree, RULE_MESSAGE);
            } else {
                super.visitMethodInvocation(tree);
            }
        }
        @Override
        public void visitNewClass(NewClassTree tree) {
            reportIssue(tree, RULE_MESSAGE);
            if (REPOSITORY_METHOD.matches(tree)) {
                reportIssue(tree, RULE_MESSAGE);
            } else {
                super.visitNewClass(tree);
            }
        }

    }
}
