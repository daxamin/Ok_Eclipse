import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.jface.text.link.LinkedPositionGroup;

import org.eclipse.ui.IEditorPart;

import org.eclipse.ltk.core.refactoring.Refactoring;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.NamingConventions;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodReference;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression.Operator;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeMethodReference;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite.TypeLocation;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.jdt.internal.core.manipulation.util.BasicElementLabels;
import org.eclipse.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility2;
import org.eclipse.jdt.internal.corext.dom.ASTNodeFactory;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.jdt.internal.corext.dom.DimensionRewrite;
import org.eclipse.jdt.internal.corext.dom.JdtASTMatcher;
import org.eclipse.jdt.internal.corext.dom.LinkedNodeFinder;
import org.eclipse.jdt.internal.corext.dom.ModifierRewrite;
import org.eclipse.jdt.internal.corext.dom.ScopeAnalyzer;
import org.eclipse.jdt.internal.corext.dom.Selection;
import org.eclipse.jdt.internal.corext.dom.SelectionAnalyzer;
import org.eclipse.jdt.internal.corext.dom.TokenScanner;
import org.eclipse.jdt.internal.corext.fix.CleanUpConstants;
import org.eclipse.jdt.internal.corext.fix.ControlStatementsFix;
import org.eclipse.jdt.internal.corext.fix.ConvertLoopFix;
import org.eclipse.jdt.internal.corext.fix.IProposableFix;
import org.eclipse.jdt.internal.corext.fix.LambdaExpressionsFix;
import org.eclipse.jdt.internal.corext.fix.LinkedProposalModel;
import org.eclipse.jdt.internal.corext.fix.TypeParametersFix;
import org.eclipse.jdt.internal.corext.fix.VariableDeclarationFix;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringAvailabilityTester;
import org.eclipse.jdt.internal.corext.refactoring.code.ConvertAnonymousToNestedRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractConstantRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractMethodRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractTempRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.code.InlineTempRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.code.PromoteTempToFieldRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.util.TightSourceRangeComputer;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.ui.cleanup.CleanUpOptions;
import org.eclipse.jdt.ui.cleanup.ICleanUp;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;
import org.eclipse.jdt.ui.text.java.correction.ASTRewriteCorrectionProposal;
import org.eclipse.jdt.ui.text.java.correction.ChangeCorrectionProposal;
import org.eclipse.jdt.ui.text.java.correction.ICommandAccess;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.fix.ControlStatementsCleanUp;
import org.eclipse.jdt.internal.ui.fix.ConvertLoopCleanUp;
import org.eclipse.jdt.internal.ui.fix.LambdaExpressionsCleanUp;
import org.eclipse.jdt.internal.ui.fix.TypeParametersCleanUp;
import org.eclipse.jdt.internal.ui.fix.VariableDeclarationCleanUp;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.correction.proposals.AssignToVariableAssistProposal;
import org.eclipse.jdt.internal.ui.text.correction.proposals.FixCorrectionProposal;
import org.eclipse.jdt.internal.ui.text.correction.proposals.GenerateForLoopAssistProposal;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedCorrectionProposal;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedNamesAssistProposal;
import org.eclipse.jdt.internal.ui.text.correction.proposals.NewDefiningMethodProposal;
import org.eclipse.jdt.internal.ui.text.correction.proposals.RefactoringCorrectionProposal;
import org.eclipse.jdt.internal.ui.text.correction.proposals.RenameRefactoringProposal;
import org.eclipse.jdt.internal.ui.text.correction.proposals.TypeChangeCorrectionProposal;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;

/**
  */
public class QuickAssistProcessor implements IQuickAssistProcessor {

	public static final String SPLIT_JOIN_VARIABLE_DECLARATION_ID= "org.eclipse.jdt.ui.correction.splitJoinVariableDeclaration.assist"; //$NON-NLS-1$
	public static final String CONVERT_FOR_LOOP_ID= "org.eclipse.jdt.ui.correction.convertForLoop.assist"; //$NON-NLS-1$
	public static final String ASSIGN_TO_LOCAL_ID= "org.eclipse.jdt.ui.correction.assignToLocal.assist"; //$NON-NLS-1$
	public static final String ASSIGN_TO_FIELD_ID= "org.eclipse.jdt.ui.correction.assignToField.assist"; //$NON-NLS-1$
	public static final String ASSIGN_PARAM_TO_FIELD_ID= "org.eclipse.jdt.ui.correction.assignParamToField.assist"; //$NON-NLS-1$
	public static final String ASSIGN_ALL_PARAMS_TO_NEW_FIELDS_ID= "org.eclipse.jdt.ui.correction.assignAllParamsToNewFields.assist"; //$NON-NLS-1$
	public static final String ADD_BLOCK_ID= "org.eclipse.jdt.ui.correction.addBlock.assist"; //$NON-NLS-1$
	public static final String EXTRACT_LOCAL_ID= "org.eclipse.jdt.ui.correction.extractLocal.assist"; //$NON-NLS-1$
	public static final String EXTRACT_LOCAL_NOT_REPLACE_ID= "org.eclipse.jdt.ui.correction.extractLocalNotReplaceOccurrences.assist"; //$NON-NLS-1$
	public static final String EXTRACT_CONSTANT_ID= "org.eclipse.jdt.ui.correction.extractConstant.assist"; //$NON-NLS-1$
	public static final String INLINE_LOCAL_ID= "org.eclipse.jdt.ui.correction.inlineLocal.assist"; //$NON-NLS-1$
	public static final String CONVERT_LOCAL_TO_FIELD_ID= "org.eclipse.jdt.ui.correction.convertLocalToField.assist"; //$NON-NLS-1$
	public static final String CONVERT_ANONYMOUS_TO_LOCAL_ID= "org.eclipse.jdt.ui.correction.convertAnonymousToLocal.assist"; //$NON-NLS-1$
	public static final String CONVERT_TO_STRING_BUFFER_ID= "org.eclipse.jdt.ui.correction.convertToStringBuffer.assist"; //$NON-NLS-1$
	public static final String CONVERT_TO_MESSAGE_FORMAT_ID= "org.eclipse.jdt.ui.correction.convertToMessageFormat.assist"; //$NON-NLS-1$;
	public static final String EXTRACT_METHOD_INPLACE_ID= "org.eclipse.jdt.ui.correction.extractMethodInplace.assist"; //$NON-NLS-1$;

	public QuickAssistProcessor() {
		super();
	}

	@Override
	public boolean hasAssists(IInvocationContext context) throws CoreException {
		ASTNode coveringNode= context.getCoveringNode();
		if (coveringNode != null) {
			ArrayList<ASTNode> coveredNodes= AdvancedQuickAssistProcessor.getFullyCoveredNodes(context, coveringNode);
			return getCatchClauseToThrowsProposals(context, coveringNode, null)
				|| getPickoutTypeFromMulticatchProposals(context, coveringNode, coveredNodes, null)
				|| getConvertToMultiCatchProposals(context, coveringNode, null)
				|| getUnrollMultiCatchProposals(context, coveringNode, null)
				|| getRenameLocalProposals(context, coveringNode, null, null)
				|| getRenameRefactoringProposal(context, coveringNode, null, null)
				|| getAssignToVariableProposals(context, coveringNode, null, null)
				|| getUnWrapProposals(context, coveringNode, null)
				|| getAssignParamToFieldProposals(context, coveringNode, null)
				|| getAssignAllParamsToFieldsProposals(context, coveringNode, null)
				|| getJoinVariableProposals(context, coveringNode, null)
				|| getAddFinallyProposals(context, coveringNode, null)
				|| getAddElseProposals(context, coveringNode, null)
				|| getSplitVariableProposals(context, coveringNode, null)
				|| getAddBlockProposals(context, coveringNode, null)
				|| getArrayInitializerToArrayCreation(context, coveringNode, null)
				|| getCreateInSuperClassProposals(context, coveringNode, null)
				|| getInvertEqualsProposal(context, coveringNode, null)
				|| getConvertForLoopProposal(context, coveringNode, null)
				|| getConvertIterableLoopProposal(context, coveringNode, null)
				|| getConvertEnhancedForLoopProposal(context, coveringNode, null)
				|| getGenerateForLoopProposals(context, coveringNode, null, null)
				|| getExtractVariableProposal(context, false, null)
				|| getExtractMethodProposal(context, coveringNode, false, null)
				|| getInlineLocalProposal(context, coveringNode, null)
				|| getConvertLocalToFieldProposal(context, coveringNode, null)
				|| getConvertAnonymousToNestedProposal(context, coveringNode, null)
				|| getConvertAnonymousClassCreationsToLambdaProposals(context, coveringNode, null)
				|| getConvertLambdaToAnonymousClassCreationsProposals(context, coveringNode, null)
				|| getChangeLambdaBodyToBlockProposal(context, coveringNode, null)
				|| getChangeLambdaBodyToExpressionProposal(context, coveringNode, null)
				|| getAddInferredLambdaParameterTypes(context, coveringNode, null)
				|| getConvertMethodReferenceToLambdaProposal(context, coveringNode, null)
				|| getConvertLambdaToMethodReferenceProposal(context, coveringNode, null)
				|| getFixParenthesesInLambdaExpression(context, coveringNode, null)
				|| getRemoveBlockProposals(context, coveringNode, null)
				|| getMakeVariableDeclarationFinalProposals(context, null)
				|| getMissingCaseStatementProposals(context, coveringNode, null)
				|| getConvertStringConcatenationProposals(context, null)
				|| getInferDiamondArgumentsProposal(context, coveringNode, null, null);
		}
		return false;
	}

	@Override
	public IJavaCompletionProposal[] getAssists(IInvocationContext context, IProblemLocation[] locations) throws CoreException {
		ASTNode coveringNode= context.getCoveringNode();
		if (coveringNode != null) {
			ArrayList<ASTNode> coveredNodes= AdvancedQuickAssistProcessor.getFullyCoveredNodes(context, coveringNode);
			ArrayList<ICommandAccess> resultingCollections= new ArrayList<>();
			boolean noErrorsAtLocation= noErrorsAtLocation(locations);

			// quick assists that show up also if there is an error/warning
			getRenameLocalProposals(context, coveringNode, locations, resultingCollections);
			getRenameRefactoringProposal(context, coveringNode, locations, resultingCollections);
			getAssignToVariableProposals(context, coveringNode, locations, resultingCollections);
			getAssignParamToFieldProposals(context, coveringNode, resultingCollections);
			getAssignAllParamsToFieldsProposals(context, coveringNode, resultingCollections);
			getInferDiamondArgumentsProposal(context, coveringNode, locations, resultingCollections);
			getGenerateForLoopProposals(context, coveringNode, locations, resultingCollections);

			if (noErrorsAtLocation) {
				boolean problemsAtLocation= locations.length != 0;
				getCatchClauseToThrowsProposals(context, coveringNode, resultingCollections);
				getPickoutTypeFromMulticatchProposals(context, coveringNode, coveredNodes, resultingCollections);
				getConvertToMultiCatchProposals(context, coveringNode, resultingCollections);
				getUnrollMultiCatchProposals(context, coveringNode, resultingCollections);
				getUnWrapProposals(context, coveringNode, resultingCollections);
				getJoinVariableProposals(context, coveringNode, resultingCollections);
				getSplitVariableProposals(context, coveringNode, resultingCollections);
				getAddFinallyProposals(context, coveringNode, resultingCollections);
				getAddElseProposals(context, coveringNode, resultingCollections);
				getAddBlockProposals(context, coveringNode, resultingCollections);
				getInvertEqualsProposal(context, coveringNode, resultingCollections);
				getArrayInitializerToArrayCreation(context, coveringNode, resultingCollections);
				getCreateInSuperClassProposals(context, coveringNode, resultingCollections);
				getExtractVariableProposal(context, problemsAtLocation, resultingCollections);
				getExtractMethodProposal(context, coveringNode, problemsAtLocation, resultingCollections);
				getInlineLocalProposal(context, coveringNode, resultingCollections);
				getConvertLocalToFieldProposal(context, coveringNode, resultingCollections);
				getConvertAnonymousToNestedProposal(context, coveringNode, resultingCollections);
				getConvertAnonymousClassCreationsToLambdaProposals(context, coveringNode, resultingCollections);
				getConvertLambdaToAnonymousClassCreationsProposals(context, coveringNode, resultingCollections);
				getChangeLambdaBodyToBlockProposal(context, coveringNode, resultingCollections);
				getChangeLambdaBodyToExpressionProposal(context, coveringNode, resultingCollections);
				getAddInferredLambdaParameterTypes(context, coveringNode, resultingCollections);
				getConvertMethodReferenceToLambdaProposal(context, coveringNode, resultingCollections);
				getConvertLambdaToMethodReferenceProposal(context, coveringNode, resultingCollections);
				getFixParenthesesInLambdaExpression(context, coveringNode, resultingCollections);
				if (!getConvertForLoopProposal(context, coveringNode, resultingCollections))
					getConvertIterableLoopProposal(context, coveringNode, resultingCollections);
				getConvertEnhancedForLoopProposal(context, coveringNode, resultingCollections);
				getRemoveBlockProposals(context, coveringNode, resultingCollections);
				getMakeVariableDeclarationFinalProposals(context, resultingCollections);
				getConvertStringConcatenationProposals(context, resultingCollections);
				getMissingCaseStatementProposals(context, coveringNode, resultingCollections);
				getConvertVarTypeToResolvedTypeProposal(context, coveringNode, resultingCollections);
				getConvertResolvedTypeToVarTypeProposal(context, coveringNode, resultingCollections);
			}
			return resultingCollections.toArray(new IJavaCompletionProposal[resultingCollections.size()]);
		}
		return null;
	}

	static boolean noErrorsAtLocation(IProblemLocation[] locations) {
		if (locations != null) {
			for (int i= 0; i < locations.length; i++) {
				IProblemLocation location= locations[i];
				if (location.isError()) {
					if (IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER.equals(location.getMarkerType())
							&& JavaCore.getOptionForConfigurableSeverity(location.getProblemId()) != null) {
						// continue (only drop out for severe (non-optional) errors)
					} else {
						return false;
					}
				}
			}
		}
		return true;
	}

	private static int getIndex(int offset, List<Statement> statements) {
		for (int i= 0; i < statements.size(); i++) {
			Statement s= statements.get(i);
			if (offset <= s.getStartPosition()) {
				return i;
			}
			if (offset < s.getStartPosition() + s.getLength()) {
				return -1;
			}
		}
		return statements.size();
	}

	private static boolean getExtractMethodProposal(IInvocationContext context, ASTNode coveringNode, boolean problemsAtLocation, Collection<ICommandAccess> proposals) throws CoreException {
		if (!(coveringNode instanceof Expression) && !(coveringNode instanceof Statement) && !(coveringNode instanceof Block)) {
			return false;
		}
		if (coveringNode instanceof Block) {
			List<Statement> statements= ((Block) coveringNode).statements();
			int startIndex= getIndex(context.getSelectionOffset(), statements);
			if (startIndex == -1)
				return false;
			int endIndex= getIndex(context.getSelectionOffset() + context.getSelectionLength(), statements);
			if (endIndex == -1 || endIndex <= startIndex)
				return false;
		}

		if (proposals == null) {
			return true;
		}

		final ICompilationUnit cu= context.getCompilationUnit();
		final ExtractMethodRefactoring extractMethodRefactoring= new ExtractMethodRefactoring(context.getASTRoot(), context.getSelectionOffset(), context.getSelectionLength());
		extractMethodRefactoring.setMethodName("extracted"); //$NON-NLS-1$
		if (extractMethodRefactoring.checkInitialConditions(new NullProgressMonitor()).isOK()) {
			String label= CorrectionMessages.QuickAssistProcessor_extractmethod_description;
			LinkedProposalModel linkedProposalModel= new LinkedProposalModel();
			extractMethodRefactoring.setLinkedProposalModel(linkedProposalModel);

			Image image= JavaPluginImages.get(JavaPluginImages.IMG_MISC_PUBLIC);
			int relevance= problemsAtLocation ? IProposalRelevance.EXTRACT_METHOD_ERROR : IProposalRelevance.EXTRACT_METHOD;
			RefactoringCorrectionProposal proposal= new RefactoringCorrectionProposal(label, cu, extractMethodRefactoring, relevance, image);
			proposal.setCommandId(EXTRACT_METHOD_INPLACE_ID);
			proposal.setLinkedProposalModel(linkedProposalModel);
			proposals.add(proposal);
		}
		return true;
	}



	private static boolean getExtractVariableProposal(IInvocationContext context, boolean problemsAtLocation, Collection<ICommandAccess> proposals) throws CoreException {
		
		ASTNode node= context.getCoveredNode();

		if (!(node instanceof Expression)) {
			if (context.getSelectionLength() != 0) {
				return false;
			}
			node= context.getCoveringNode();
			if (!(node instanceof Expression)) {
				return false;
			}
		}
		final Expression expression= (Expression) node;

		ITypeBinding binding= expression.resolveTypeBinding();
		if (binding == null || Bindings.isVoidType(binding)) {
			return false;
		}
		if (proposals == null) {
			return true;
		}

		final ICompilationUnit cu= context.getCompilationUnit();
		ExtractTempRefactoring extractTempRefactoring= new ExtractTempRefactoring(context.getASTRoot(), context.getSelectionOffset(), context.getSelectionLength());
		if (extractTempRefactoring.checkInitialConditions(new NullProgressMonitor()).isOK()) {
			extractTempRefactoring.setReplaceAllOccurrences(true);
			LinkedProposalModel linkedProposalModel= new LinkedProposalModel();
			extractTempRefactoring.setLinkedProposalModel(linkedProposalModel);
			extractTempRefactoring.setCheckResultForCompileProblems(false);

			String label= CorrectionMessages.QuickAssistProcessor_extract_to_local_all_description;
			Image image= JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_LOCAL);
			int relevance;
			if (context.getSelectionLength() == 0) {
				relevance= IProposalRelevance.EXTRACT_LOCAL_ALL_ZERO_SELECTION;
			} else if (problemsAtLocation) {
				relevance= IProposalRelevance.EXTRACT_LOCAL_ALL_ERROR;
			} else {
				relevance= IProposalRelevance.EXTRACT_LOCAL_ALL;
			}
			RefactoringCorrectionProposal proposal= new RefactoringCorrectionProposal(label, cu, extractTempRefactoring, relevance, image) {
				@Override
				protected void init(Refactoring refactoring) throws CoreException {
					ExtractTempRefactoring etr= (ExtractTempRefactoring) refactoring;
					etr.setTempName(etr.guessTempName()); // expensive
				}
			};
			proposal.setCommandId(EXTRACT_LOCAL_ID);
			proposal.setLinkedProposalModel(linkedProposalModel);
			proposals.add(proposal);
		}

		ExtractTempRefactoring extractTempRefactoringSelectedOnly= new ExtractTempRefactoring(context.getASTRoot(), context.getSelectionOffset(), context.getSelectionLength());
		extractTempRefactoringSelectedOnly.setReplaceAllOccurrences(false);
		if (extractTempRefactoringSelectedOnly.checkInitialConditions(new NullProgressMonitor()).isOK()) {
			LinkedProposalModel linkedProposalModel= new LinkedProposalModel();
			extractTempRefactoringSelectedOnly.setLinkedProposalModel(linkedProposalModel);
			extractTempRefactoringSelectedOnly.setCheckResultForCompileProblems(false);

			String label= CorrectionMessages.QuickAssistProcessor_extract_to_local_description;
			Image image= JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_LOCAL);
			int relevance;
			if (context.getSelectionLength() == 0) {
				relevance= IProposalRelevance.EXTRACT_LOCAL_ZERO_SELECTION;
			} else if (problemsAtLocation) {
				relevance= IProposalRelevance.EXTRACT_LOCAL_ERROR;
			} else {
				relevance= IProposalRelevance.EXTRACT_LOCAL;
			}
			RefactoringCorrectionProposal proposal= new RefactoringCorrectionProposal(label, cu, extractTempRefactoringSelectedOnly, relevance, image) {
				@Override
				protected void init(Refactoring refactoring) throws CoreException {
					ExtractTempRefactoring etr= (ExtractTempRefactoring) refactoring;
					etr.setTempName(etr.guessTempName()); // expensive
				}
			};
			proposal.setCommandId(EXTRACT_LOCAL_NOT_REPLACE_ID);
			proposal.setLinkedProposalModel(linkedProposalModel);
			proposals.add(proposal);
		}

}