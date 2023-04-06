package fr.greencodeinitiative.java.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.MethodMatchers;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.NewClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.VariableTree;

@Rule(key = "EC1",
        name = "Developpement",
        description = Coookie2Check.RULE_MESSAGE,
        priority = Priority.MINOR,
        tags = {"bug"})
public class Coookie2Check extends IssuableSubscriptionVisitor {

    protected static final String RULE_MESSAGE = "Avoid Spring repository call in loop";

//    private static final String COOKIE_CLASS = "javax.servlet.http.Cookie";
    private static final String COOKIE_CLASS = "Cookie";
    private static final String COOKIE_METHOD_NAME = "setMaxAge";
    private static final MethodMatchers COOKIE_METHOD =
            MethodMatchers.create().ofSubTypes(COOKIE_CLASS).names(COOKIE_METHOD_NAME).withAnyParameters()
            .build();

    private final NewClassVisitor newClassVisitor = new NewClassVisitor();
    private final MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor();
    
    private BlockTree blocVisited;
    private String nameOfCookieInstance;

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(Tree.Kind.BLOCK);
    }

    @Override
    public void visitNode(Tree tree) {
    	blocVisited = (BlockTree) tree;
        tree.accept(newClassVisitor);
        if (newClassVisitor.cookieFound) {

        	System.out.printf("!!!!! cookie found !!!!! in tree %s \n", tree);
        	System.out.println("searching method setMaxAge....");
        	// find setMaxAge method in the block or report an Issue
        	blocVisited.accept(methodInvocationVisitor);
        	if (methodInvocationVisitor.methodFound) {
            	System.out.println("searching method setMaxAge.... FOUND !!! ");
        	} else {
        		System.out.println("searching method setMaxAge.... NOT FOUND !!!");
        	}
        }
    }

    private class NewClassVisitor extends BaseTreeVisitor {
    	
    	private boolean cookieFound = false;
    	private List<Tree> reportTrees = new ArrayList<Tree>();
    	
        @Override
        public void visitNewClass(NewClassTree tree) {
            //reportIssue(tree, RULE_MESSAGE);
        	
//            if (tree.identifier().symbolType().isSubtypeOf(COOKIE_CLASS)) {
        	if (tree.identifier().symbolType().name().equals(COOKIE_CLASS)) {
            	
//            	System.out.printf("!!!!! cookie found !!!!! in tree %s \n", tree);
            	
            	Tree parent = tree.parent();
//            	System.out.println("parent : " + parent);
//            	System.out.println("parent : " + ((VariableTree) parent).simpleName());
            	
            	// recuperation du nom de l'instance du cookie
            	nameOfCookieInstance = ((VariableTree) parent).simpleName().name();
            	
//            	for (Tree tree : ((VariableTree) parent).simpleName()) {
//					
//				}
//            	System.out.println(parent);
            	
            	cookieFound = true;
            	reportTrees.add(tree);
            } else {
                super.visitNewClass(tree);
            }
        }
    }
    
    private class MethodInvocationVisitor extends BaseTreeVisitor {
    	
        private boolean methodFound = false;
    	private List<Tree> reportTrees = new ArrayList<Tree>();
    	
        @Override
        public void visitMethodInvocation(MethodInvocationTree tree) {
        	
        	//if (COOKIE_METHOD.matches(tree)) { ne marche pas
        	if (tree.methodSelect().is(Kind.MEMBER_SELECT)) {
	        	MemberSelectExpressionTree member = ((MemberSelectExpressionTree)tree.methodSelect());
	        	if (member.expression().firstToken().text().equals(nameOfCookieInstance) &&
	        			member.identifier().firstToken().text().equals(COOKIE_METHOD_NAME)) {
	        		
	        		methodFound = true;
	        	}
            } else {
            	//TODO pas de report ici mais dans visitNode si methodFound toujours false
                reportIssue(tree, RULE_MESSAGE);
                super.visitMethodInvocation(tree);
            }
        }
    }

}
