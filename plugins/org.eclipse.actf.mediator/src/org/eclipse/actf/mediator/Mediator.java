/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.mediator;

import java.util.HashMap;
import java.util.Vector;

import org.eclipse.actf.model.ui.IModelServiceHolder;
import org.eclipse.actf.util.ui.AbstractPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;

public class Mediator {

	private static Mediator instance = new Mediator();

	private IACTFReportGenerator curRepoter;

	private IModelServiceHolder currentModelServiceHolder;

	// TODO model/vizview combination to store the result
	private HashMap<IACTFReportGenerator, IACTFReport> reportMap = new HashMap<IACTFReportGenerator, IACTFReport>();

	private Vector<IMediatorEventListener> mediatorEventLisnterV = new Vector<IMediatorEventListener>();

	public static Mediator getInstance() {
		return instance;
	}
	
    private Mediator() {
		init();
	}

	public void setEvaluationResult(IACTFReportGenerator view,
			IACTFReport report) {
		reportMap.put(view, report);
		reportChanged(new MediatorEvent(this, currentModelServiceHolder, view,
				report));
	}

	public IACTFReport getEvaluationResult(IACTFReportGenerator reporter) {
		return getEvaluationResult(null, reporter);
	}

	private IACTFReport getEvaluationResult(IModelServiceHolder holder,
			IACTFReportGenerator reporter) {
		if (reportMap.containsKey(reporter)) {
			return ((IACTFReport) reportMap.get(reporter));
		}
		return null;
		// return (new EvaluationResultImpl());
	}

	private void init() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		IViewReference[] views = activePage.getViewReferences();
		IViewPart tmpViewPart;
		for (int i = 0; i < views.length; i++) {
			if ((tmpViewPart = views[i].getView(false)) != null) {
				if (tmpViewPart instanceof IMediatorEventListener) {
					mediatorEventLisnterV
							.add((IMediatorEventListener) tmpViewPart);
				}
			}
		}

		activePage.addPartListener(new AbstractPartListener() {

			public void partActivated(IWorkbenchPartReference partRef) {
				IWorkbenchPart part = partRef.getPart(false);
				if (part instanceof IACTFReportGenerator && part != curRepoter) {
					curRepoter = (IACTFReportGenerator) part;
					reporterViewChanged(new MediatorEvent(Mediator.this,
							currentModelServiceHolder, curRepoter,
							getEvaluationResult(curRepoter)));
				}
				if (part instanceof IModelServiceHolder
						&& part != currentModelServiceHolder) {
					currentModelServiceHolder = (IModelServiceHolder) part;
					modelserviceChanged(new MediatorEvent(Mediator.this,
							currentModelServiceHolder, curRepoter,
							getEvaluationResult(curRepoter)));
				}
			}

			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
				IWorkbenchPart part = partRef.getPart(false);
				if (part instanceof IMediatorEventListener) {
					mediatorEventLisnterV.remove((IMediatorEventListener) part);
				}
			}

			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
				IWorkbenchPart part = partRef.getPart(false);
				if (part instanceof IACTFReportViewer) {
					IACTFReportViewer viewer = (IACTFReportViewer) part;
					if (currentModelServiceHolder != null) {
						viewer.reportChanged(new MediatorEvent(Mediator.this,
								currentModelServiceHolder, curRepoter,
								getEvaluationResult(curRepoter)));
					}
					mediatorEventLisnterV.add(viewer);
				} else if (part instanceof IACTFReportGenerator) {
					mediatorEventLisnterV.add((IACTFReportGenerator) part);
				}
			}

			// TODO Call InputChanged when the target URL of the Editor changes
			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {
				IWorkbenchPart part = partRef.getPart(false);
				if (part instanceof IModelServiceHolder) {
					currentModelServiceHolder = (IModelServiceHolder) part;
					modelserviceInputChanged(new MediatorEvent(Mediator.this,
							currentModelServiceHolder, curRepoter,
							getEvaluationResult(curRepoter)));
				}
			}

		});

	}

	public boolean addMediatorEventListener(IMediatorEventListener listener) {
		return mediatorEventLisnterV.add(listener);
	}

	public boolean removeMediatorEventListener(IMediatorEventListener listener) {
		return mediatorEventLisnterV.remove(listener);
	}

	private void modelserviceChanged(MediatorEvent event) {
		for (IMediatorEventListener i : mediatorEventLisnterV) {
			i.modelserviceChanged(event);
		}
	}

	private void modelserviceInputChanged(MediatorEvent event) {
		for (IMediatorEventListener i : mediatorEventLisnterV) {
			i.modelserviceInputChanged(event);
		}
	}

	private void reportChanged(MediatorEvent event) {
		for (IMediatorEventListener i : mediatorEventLisnterV) {
			i.reportChanged(event);
		}
	}

	private void reporterViewChanged(MediatorEvent event) {
		for (IMediatorEventListener i : mediatorEventLisnterV) {
			i.reportGeneratorChanged(event);
		}
	}

}
