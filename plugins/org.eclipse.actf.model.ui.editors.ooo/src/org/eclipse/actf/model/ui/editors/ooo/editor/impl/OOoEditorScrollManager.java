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

package org.eclipse.actf.model.ui.editors.ooo.editor.impl;

import org.eclipse.actf.model.ModelServiceSizeInfo;
import org.eclipse.actf.model.IModelServiceScrollManager;
import org.eclipse.actf.model.dom.odf.ODFConstants.ContentType;
import org.eclipse.actf.model.dom.odf.util.ODFFileUtils;
import org.eclipse.actf.model.ui.editors.ooo.internal.util.ODFException;

import com.sun.star.accessibility.XAccessibleAction;
import com.sun.star.lang.IndexOutOfBoundsException;



public class OOoEditorScrollManager implements IModelServiceScrollManager {

    OOoWindowComposite _oOoWindowComposite;

    private int scrollbarWidth = 0;

    public OOoEditorScrollManager(OOoWindowComposite oOoWindowComposite) {
        _oOoWindowComposite = oOoWindowComposite;
    }

    public void absoluteCoordinateScroll(int y, boolean waitRendering) {
    }

    public void absoluteCoordinateScroll(int x, int y, boolean waitRendering) {
    }

    public int incrementScrollX(boolean waitRendering) {
        String sUrl = _oOoWindowComposite.getUrl();
        if (ContentType.SPREADSHEET.equals(ODFFileUtils.getODFFileType(sUrl))) {
            return -1;
        } else {
            return doScrollAction(0, OOoWindowComposite.ACTION_SCROLL_INCREMENT_LINE);
        }
    }

    public int decrementScrollX(boolean waitRendering) {
        String sUrl = _oOoWindowComposite.getUrl();
        if (ContentType.SPREADSHEET.equals(ODFFileUtils.getODFFileType(sUrl))) {
            return -1;
        } else {
            return doScrollAction(0, OOoWindowComposite.ACTION_SCROLL_DECREMENT_LINE);
        }
    }

    public int incrementScrollY(boolean waitRendering) {
        String sUrl = _oOoWindowComposite.getUrl();
        if (ContentType.SPREADSHEET.equals(ODFFileUtils.getODFFileType(sUrl))) {
            return -1;
        } else {
            return doScrollAction(1, OOoWindowComposite.ACTION_SCROLL_INCREMENT_LINE);
        }
    }

    public int decrementScrollY(boolean waitRendering) {
        String sUrl = _oOoWindowComposite.getUrl();
        if (ContentType.SPREADSHEET.equals(ODFFileUtils.getODFFileType(sUrl))) {
            return -1;
        } else {
            return doScrollAction(1, OOoWindowComposite.ACTION_SCROLL_DECREMENT_LINE);
        }
    }

    public int incrementLargeScrollX(boolean waitRendering) {
        String sUrl = _oOoWindowComposite.getUrl();
        ContentType odfFileType = ODFFileUtils.getODFFileType(sUrl);
        if ((ContentType.SPREADSHEET.equals(odfFileType))
                ||(ContentType.PRESENTATION.equals(odfFileType))) {
            return -1;
        } else {
            return doScrollAction(0, OOoWindowComposite.ACTION_SCROLL_INCREMENT_BLOCK);
        }
    }

    public int decrementLargeScrollX(boolean waitRendering) {
        String sUrl = _oOoWindowComposite.getUrl();
        ContentType odfFileType = ODFFileUtils.getODFFileType(sUrl);
        if ((ContentType.SPREADSHEET.equals(odfFileType))
                ||(ContentType.PRESENTATION.equals(odfFileType))) {
            return -1;
        } else {
            return doScrollAction(0, OOoWindowComposite.ACTION_SCROLL_DECREMENT_BLOCK);
        }
    }

    public int incrementLargeScrollY(boolean waitRendering) {
        String sUrl = _oOoWindowComposite.getUrl();
        ContentType odfFileType = ODFFileUtils.getODFFileType(sUrl);
        if (ContentType.PRESENTATION.equals(odfFileType)) {
        	_oOoWindowComposite.setDrawingMode();        	
            return _oOoWindowComposite.movePresentationPage(true);
        } else if (ContentType.SPREADSHEET.equals(odfFileType)) {
            return -1;
        } else {
            return doScrollAction(1, OOoWindowComposite.ACTION_SCROLL_INCREMENT_BLOCK);
        }
    }

    public int decrementLargeScrollY(boolean waitRendering) {
        String sUrl = _oOoWindowComposite.getUrl();
        ContentType odfFileType = ODFFileUtils.getODFFileType(sUrl);
        if (ContentType.PRESENTATION.equals(odfFileType)) {
        	_oOoWindowComposite.setDrawingMode();     	
            return _oOoWindowComposite.movePresentationPage(false);
        } else if (ContentType.SPREADSHEET.equals(odfFileType)) {
            return -1;
        } else {
            return doScrollAction(1, OOoWindowComposite.ACTION_SCROLL_DECREMENT_BLOCK);
        }
    }

    public int decrementPageScroll(boolean waitRendering) {
    	String sUrl = _oOoWindowComposite.getUrl();
        ContentType odfFileType = ODFFileUtils.getODFFileType(sUrl);
        if (ContentType.PRESENTATION.equals(odfFileType)) {
        	_oOoWindowComposite.setDrawingMode();
            int result = _oOoWindowComposite.movePresentationPage(false);
            if(waitRendering) waitRendering();
            return result;
        }
        return -1;
    }

    public int incrementPageScroll(boolean waitRendering) {
        String sUrl = _oOoWindowComposite.getUrl();
        ContentType odfFileType = ODFFileUtils.getODFFileType(sUrl);
        if (ContentType.PRESENTATION.equals(odfFileType)) {
        	_oOoWindowComposite.setDrawingMode();
            int result = _oOoWindowComposite.movePresentationPage(true);
            if(waitRendering) waitRendering();
            return result;
        }
        return -1;
    }
        
    public int jumpToPage(int pageNumber, boolean waitRendering) {
    	if (ContentType.PRESENTATION.equals(ODFFileUtils.getODFFileType(_oOoWindowComposite.getUrl()))) {
    		_oOoWindowComposite.setDrawingMode();
    		int result = _oOoWindowComposite.jumpToPresentationPage(pageNumber);    		
            if(waitRendering) waitRendering();
            return result;
        }    	
        return -1;
    }

    public int getCurrentPageNumber() {
    	if (ContentType.PRESENTATION.equals(ODFFileUtils.getODFFileType(_oOoWindowComposite.getUrl()))) {
    		return _oOoWindowComposite.getCurrentPageNumber();
        }    	
        return -1;
    }
    
    public int getLastPageNumber() {
        if (ContentType.PRESENTATION.equals(ODFFileUtils.getODFFileType(_oOoWindowComposite.getUrl()))) {
        	return _oOoWindowComposite.getPresentationPageCount();
        }
    	return -1;
    }

    public int getScrollType() {
        String sUrl = _oOoWindowComposite.getUrl();
        if (sUrl == null)
            return NONE;

        if (ContentType.PRESENTATION.equals(ODFFileUtils.getODFFileType(sUrl))) {
            return PAGE;
        } else {
            return NONE;
        }
    }

    private int doScrollAction(int axis, int action) {
        int[] prevViewData = _oOoWindowComposite.getViewData();
        if (prevViewData == null)
            return -1;
        
        XAccessibleAction[] scrollAction = _oOoWindowComposite.getScrollAction();
        if((scrollAction==null)||(scrollAction.length<=axis)||(scrollAction[axis]==null)) {
            return -1;
        }
        
        try {
            scrollAction[axis].doAccessibleAction(action);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        int[] viewData = _oOoWindowComposite.getViewData();
        return Math.abs(viewData[axis] - prevViewData[axis]);
    }

    public ModelServiceSizeInfo getSize(boolean isWhole) {
        // initialize window setting
        if (ContentType.PRESENTATION.equals(ODFFileUtils.getODFFileType(_oOoWindowComposite.getUrl()))) {
            _oOoWindowComposite.setDrawingMode();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // start calculate window size
        int[] winSize;
        try {
            winSize = _oOoWindowComposite.getOOoWinSize();
        } catch (ODFException e) {
            return (new ModelServiceSizeInfo(0, 0, 0, 0));
        }
        ModelServiceSizeInfo viewSize = new ModelServiceSizeInfo(winSize[0] - scrollbarWidth, winSize[1] - scrollbarWidth,
                winSize[0] - scrollbarWidth, winSize[1] - scrollbarWidth);

        if (isWhole) {
            if (ContentType.PRESENTATION.equals(ODFFileUtils.getODFFileType(_oOoWindowComposite.getUrl()))) {
                int pageCount = _oOoWindowComposite.getPresentationPageCount();
                ModelServiceSizeInfo size = new ModelServiceSizeInfo(winSize[0] - scrollbarWidth, winSize[1]
                        - scrollbarWidth, winSize[0] - scrollbarWidth, winSize[1] * pageCount - scrollbarWidth);
                return size;
            }

            _oOoWindowComposite.setVisible(false);

            while (true) {
                int offset = incrementLargeScrollX(false);
                if ((offset == 0) || (offset == -1))
                    break;
            }
            while (true) {
                int offset = incrementLargeScrollY(false);
                if ((offset == 0) || (offset == -1))
                    break;
            }

            int[] viewData = _oOoWindowComposite.getViewData();
            if (viewData == null) {
                _oOoWindowComposite.setVisible(true);
                return viewSize;
            }
            ModelServiceSizeInfo size = new ModelServiceSizeInfo(winSize[0] - scrollbarWidth, winSize[1] - scrollbarWidth,
                    viewData[2] - scrollbarWidth, viewData[3] - scrollbarWidth);

            while (true) {
                int offset = decrementLargeScrollX(false);
                if ((offset == 0) || (offset == -1))
                    break;
            }
            while (true) {
                int offset = decrementLargeScrollY(false);
                if ((offset == 0) || (offset == -1))
                    break;
            }
            _oOoWindowComposite.setVisible(true);
            _oOoWindowComposite.redraw();
            return size;
        } else {
            return viewSize;
        }
    }

    public void setScrollBarWidth(int width) {
        scrollbarWidth = width;
    }

    private void waitRendering(){
        Thread tmp = new Thread() {
            public void run() {
                try {
                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        tmp.run();
        try {                    
            tmp.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
}
