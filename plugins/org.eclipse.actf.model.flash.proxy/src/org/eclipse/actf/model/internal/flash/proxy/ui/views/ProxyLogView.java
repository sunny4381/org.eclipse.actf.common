/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.internal.flash.proxy.ui.views;

import java.util.Date;
import java.util.logging.Level;

import org.eclipse.actf.model.internal.flash.proxy.Messages;
import org.eclipse.actf.model.internal.flash.proxy.ProxyPlugin;
import org.eclipse.actf.model.internal.flash.proxy.logs.ProxyLogHandler;
import org.eclipse.actf.model.internal.flash.proxy.logs.ProxyLogRecord;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.ibm.icu.text.MessageFormat;

public class ProxyLogView extends ViewPart {
	public static final String ID = ProxyLogView.class.getName();

	private static final String[] HEADINGS = { Messages.proxy_message, "ID",
			Messages.proxy_source, Messages.proxy_time };
	private static final int[] WEIGHTS = { 30, 3, 10, 5 };
	private static final int[] ALIGNMENTS = { SWT.LEFT, SWT.LEFT, SWT.LEFT,
			SWT.LEFT };
	private static final int ROW_MESSAGE = 0;
	private static final int ROW_ID = 1;
	private static final int ROW_SOURCE = 2;
	private static final int ROW_TIME = 3;

	private TableViewer viewer;
	private ProxyLogViewComparator sorter = new ProxyLogViewComparator();

	private Action clearAction;
	private Action showFineAction;
	private Action copyAction;
	private ViewLabelProvider labelProvider;
	private static final ISharedImages sharedImages = PlatformUI.getWorkbench()
			.getSharedImages();

	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return ProxyLogHandler.getLogs().toArray();
		}
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			if (obj instanceof ProxyLogRecord) {
				switch (index) {
				case ROW_TIME:
					long time = ((ProxyLogRecord) obj).getMillis();
					return MessageFormat.format(
							"{0,time}", new Object[] { new Date(time) }); //$NON-NLS-1$
				case ROW_MESSAGE:
					return ((ProxyLogRecord) obj).getMessage();
				case ROW_ID:
					return ((ProxyLogRecord) obj).getID();
				case ROW_SOURCE:
					String logger = ((ProxyLogRecord) obj).getLoggerName();
					int sep = logger.lastIndexOf('.');
					if (-1 != sep) {
						return logger.substring(sep + 1);
					}
					return logger;
				}
			}
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			if (ROW_MESSAGE == index && obj instanceof ProxyLogRecord) {
				ISharedImages sharedImages = PlatformUI.getWorkbench()
						.getSharedImages();
				int level = ((ProxyLogRecord) obj).getLevel().intValue();
				if (level > Level.WARNING.intValue()) {
					return sharedImages
							.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
				}
				if (level > Level.INFO.intValue()) {
					return sharedImages
							.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
				}
				if (level > Level.FINE.intValue()) {
					return sharedImages
							.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
				}
			}
			return null;
		}
	}

	/**
	 * The constructor.
	 */
	public ProxyLogView() {
	}

	public void refresh() {
		viewer.refresh();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		Table table = new Table(parent, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		TableLayout layout = new TableLayout();
		table.setLayout(layout);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		for (int i = 0; i < HEADINGS.length; i++) {
			layout.addColumnData(new ColumnWeightData(WEIGHTS[i]));
			TableColumn tc = new TableColumn(table, SWT.NONE);
			tc.setText(HEADINGS[i]);
			tc.setAlignment(ALIGNMENTS[i]);
			tc.setResizable(true);
			final int newSortingColumn = i + 1;
			tc.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (newSortingColumn == sorter.sortingColumn) {
						sorter.sortingColumn = -newSortingColumn;
					} else {
						sorter.sortingColumn = newSortingColumn;
					}
					refresh();
				}
			});
		}

		viewer = new TableViewer(table);

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(labelProvider = new ViewLabelProvider());
		viewer.setComparator(sorter);
		viewer.setInput(getViewSite());
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private class ProxyLogViewComparator extends ViewerComparator {
		public int sortingColumn = 0;

		public int compare(Viewer viewer, Object e1, Object e2) {
			if (0 != sortingColumn && viewer instanceof TableViewer) {
				IBaseLabelProvider labelProvider = ((TableViewer) viewer)
						.getLabelProvider();
				if (labelProvider instanceof ITableLabelProvider) {
					int columnIndex = Math.abs(sortingColumn) - 1;
					String s1 = ((ITableLabelProvider) labelProvider)
							.getColumnText(e1, columnIndex);
					String s2 = ((ITableLabelProvider) labelProvider)
							.getColumnText(e2, columnIndex);
					int result = 0;
					switch (columnIndex) {
					case ROW_TIME:
						result = (int) (((ProxyLogRecord) e1).getMillis() - ((ProxyLogRecord) e2)
								.getMillis());
						break;
					case ROW_MESSAGE:
					case ROW_SOURCE:
					case ROW_ID:
						result = super.compare(viewer, s1, s2);
						break;
					}
					return sortingColumn > 0 ? result : -result;
				}
			}
			return 0;
		}
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ProxyLogView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(showFineAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(clearAction);
		manager.add(copyAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(clearAction);
	}

	private void makeActions() {
		clearAction = new Action(Messages.proxy_clear) {
			public void run() {
				ProxyLogHandler.clear();
				viewer.refresh();
			}
		};
		clearAction.setToolTipText(Messages.proxy_clear_tip);
		clearAction.setImageDescriptor(ProxyPlugin.IMAGE_CLEAR);

		showFineAction = new Action(Messages.proxy_show_fine,
				Action.AS_CHECK_BOX) {
			public void run() {
				ProxyLogHandler
						.setLogLevel(showFineAction.isChecked() ? Level.FINE
								: Level.INFO);
			}
		};

		copyAction = new Action(Messages.proxy_copy) {
			public void run() {
				StringBuffer sb = new StringBuffer();
				for (int index = 0;; index++) {
					Object element = viewer.getElementAt(index);
					if (null == element) {
						break;
					}
					for (int col = 0; col < HEADINGS.length; col++) {
						sb.append(labelProvider.getColumnText(element, col));
						sb.append(col == HEADINGS.length - 1 ? "\r\n" : "\t");
					}
				}
				if (sb.length() > 0) {
					new Clipboard(Display.getCurrent()).setContents(
							new Object[] { sb.toString() },
							new Transfer[] { TextTransfer.getInstance() });
				}
			}
		};
		copyAction.setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
