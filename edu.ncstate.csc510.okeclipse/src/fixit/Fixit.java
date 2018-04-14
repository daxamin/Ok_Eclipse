package fixit;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;
import org.eclipse.jface.text.quickassist.QuickAssistAssistant;

public class Fixit implements IQuickFixProcessor{
	
	public Fixit() {
		System.out.println("Fixit constructor called.");
	}
	
	public void fix() {
		System.out.println("Fix method called");
		String msg = null;
		try {
		QuickAssistAssistant assistAssistant = new QuickAssistAssistant();
		msg = assistAssistant.showPossibleQuickAssists();
		}catch(Exception e) {
			e.printStackTrace();
		}
		if (msg == null) {
			System.out.println("msg is null");
		}else {
			System.out.println("msg: " + msg);
		}
	}

	@Override
	public IJavaCompletionProposal[] getCorrections(IInvocationContext arg0, IProblemLocation[] arg1)
			throws CoreException {
		System.out.println("getCorrections called");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCorrections(ICompilationUnit arg0, int arg1) {
		System.out.println("hasCorrections called");
		// TODO Auto-generated method stub
		return false;
	}
	

}
