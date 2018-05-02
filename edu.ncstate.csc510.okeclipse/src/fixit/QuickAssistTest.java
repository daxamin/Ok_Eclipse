package fixit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.NamingConventions;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite.TypeLocation;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.GenericVisitor;
import org.eclipse.jdt.internal.corext.dom.LinkedNodeFinder;
import org.eclipse.jdt.internal.corext.dom.NecessaryParenthesesChecker;
import org.eclipse.jdt.internal.corext.dom.StatementRewrite;
import org.eclipse.jdt.internal.corext.fix.CleanUpConstants;
import org.eclipse.jdt.internal.corext.fix.ExpressionsFix;
import org.eclipse.jdt.internal.corext.fix.IProposableFix;
import org.eclipse.jdt.internal.corext.refactoring.code.Invocations;
import org.eclipse.jdt.internal.corext.refactoring.code.OperatorPrecedence;
import org.eclipse.jdt.internal.corext.refactoring.util.NoCommentSourceRangeComputer;
import org.eclipse.jdt.internal.corext.refactoring.util.TightSourceRangeComputer;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.ui.cleanup.CleanUpOptions;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;
import org.eclipse.jdt.ui.text.java.correction.ASTRewriteCorrectionProposal;
import org.eclipse.jdt.ui.text.java.correction.ICommandAccess;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.fix.ExpressionsCleanUp;
import org.eclipse.jdt.internal.ui.text.correction.proposals.FixCorrectionProposal;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedCorrectionProposal;


public class QuickAssistTest implements IQuickAssistProcesor {
	
	public QuickAssistTest() {
		super();
	}
	
	@Override
	public boolean hasAssists(IInvocationContext context) throws CoreException {
		ASTNode coveringNode= context.getCoveringNode();
		if (coveringNode != null) {
			ArrayList<ASTNode> coveredNodes= getFullyCoveredNodes(context, coveringNode);
			return getConvertToIfReturnProposals(context, coveringNode, null)
					|| getInverseIfProposals(context, coveringNode, null)
					|| getIfReturnIntoIfElseAtEndOfVoidMethodProposals(context, coveringNode, null)
					|| getInverseIfContinueIntoIfThenInLoopsProposals(context, coveringNode, null)
					|| getInverseIfIntoContinueInLoopsProposals(context, coveringNode, null)
					|| getInverseConditionProposals(context, coveringNode, coveredNodes, null)
					|| getRemoveExtraParenthesesProposals(context, coveringNode, coveredNodes, null)
					|| getAddParanoidalParenthesesProposals(context, coveredNodes, null)
					|| getAddParenthesesForExpressionProposals(context, coveringNode, null)
					|| getJoinAndIfStatementsProposals(context, coveringNode, null)
					|| getSplitAndConditionProposals(context, coveringNode, null)
					|| getJoinOrIfStatementsProposals(context, coveringNode, coveredNodes, null)
					|| getSplitOrConditionProposals(context, coveringNode, null)
					|| getInverseConditionalExpressionProposals(context, coveringNode, null)
					|| getExchangeInnerAndOuterIfConditionsProposals(context, coveringNode, null)
					|| getExchangeOperandsProposals(context, coveringNode, null)
					|| getCastAndAssignIfStatementProposals(context, coveringNode, null)
					|| getCombineStringProposals(context, coveringNode, null)
					|| getPickOutStringProposals(context, coveringNode, null)
					|| getReplaceIfElseWithConditionalProposals(context, coveringNode, null)
					|| getReplaceConditionalWithIfElseProposals(context, coveringNode, null)
					|| getInverseLocalVariableProposals(context, coveringNode, null)
					|| getPushNegationDownProposals(context, coveringNode, null)
					|| getPullNegationUpProposals(context, coveredNodes, null)
					|| getJoinIfListInIfElseIfProposals(context, coveringNode, coveredNodes, null)
					|| getConvertSwitchToIfProposals(context, coveringNode, null)
					|| getConvertIfElseToSwitchProposals(context, coveringNode, null)
					|| GetterSetterCorrectionSubProcessor.addGetterSetterProposal(context, coveringNode, null, null);
		}
		return false;
		
		@Override
		public IJavaCompletionProposal[] getAssists(IInvocationContext context, IProblemLocation[] locations) throws CoreException {
			ASTNode coveringNode= context.getCoveringNode();
			if (coveringNode != null) {
				ArrayList<ASTNode> coveredNodes= getFullyCoveredNodes(context, coveringNode);
				ArrayList<ICommandAccess> resultingCollections= new ArrayList<>();

				//quick assists that show up also if there is an error/warning
				getReplaceConditionalWithIfElseProposals(context, coveringNode, resultingCollections);

				if (QuickAssistProcessor.noErrorsAtLocation(locations)) {
					getConvertToIfReturnProposals(context, coveringNode, resultingCollections);
					getInverseIfProposals(context, coveringNode, resultingCollections);
					getIfReturnIntoIfElseAtEndOfVoidMethodProposals(context, coveringNode, resultingCollections);
					getInverseIfContinueIntoIfThenInLoopsProposals(context, coveringNode, resultingCollections);
					getInverseIfIntoContinueInLoopsProposals(context, coveringNode, resultingCollections);
					getInverseConditionProposals(context, coveringNode, coveredNodes, resultingCollections);
					getRemoveExtraParenthesesProposals(context, coveringNode, coveredNodes, resultingCollections);
					getAddParanoidalParenthesesProposals(context, coveredNodes, resultingCollections);
					getAddParenthesesForExpressionProposals(context, coveringNode, resultingCollections);
					getJoinAndIfStatementsProposals(context, coveringNode, resultingCollections);
					getSplitAndConditionProposals(context, coveringNode, resultingCollections);
					getJoinOrIfStatementsProposals(context, coveringNode, coveredNodes, resultingCollections);
					getSplitOrConditionProposals(context, coveringNode, resultingCollections);
					getInverseConditionalExpressionProposals(context, coveringNode, resultingCollections);
					getExchangeInnerAndOuterIfConditionsProposals(context, coveringNode, resultingCollections);
					getExchangeOperandsProposals(context, coveringNode, resultingCollections);
					getCastAndAssignIfStatementProposals(context, coveringNode, resultingCollections);
					getCombineStringProposals(context, coveringNode, resultingCollections);
					getPickOutStringProposals(context, coveringNode, resultingCollections);
					getReplaceIfElseWithConditionalProposals(context, coveringNode, resultingCollections);
					getInverseLocalVariableProposals(context, coveringNode, resultingCollections);
					getPushNegationDownProposals(context, coveringNode, resultingCollections);
					getPullNegationUpProposals(context, coveredNodes, resultingCollections);
					getJoinIfListInIfElseIfProposals(context, coveringNode, coveredNodes, resultingCollections);
					getConvertSwitchToIfProposals(context, coveringNode, resultingCollections);
					getConvertIfElseToSwitchProposals(context, coveringNode, resultingCollections);
					GetterSetterCorrectionSubProcessor.addGetterSetterProposal(context, coveringNode, locations, resultingCollections);
				}

				return resultingCollections.toArray(new IJavaCompletionProposal[resultingCollections.size()]);
			}
			return null;
	}
}

}
