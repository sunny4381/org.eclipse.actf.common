/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.ui.editor.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.actf.model.internal.ui.FavoritesChangeEvent;
import org.eclipse.actf.model.internal.ui.FavoritesChangeListener;
import org.eclipse.actf.model.internal.ui.FavoritesUtil;
import org.eclipse.actf.model.ui.util.ModelServiceMessages;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchWindow;


public class FavoritesMenu extends MenuManager implements FavoritesChangeListener {

	private IWorkbenchWindow _window;
	
	private List<String> _favoritesItemIdList = new ArrayList<String>();
    
    private boolean useExistingEditor;
	
	public FavoritesMenu(IWorkbenchWindow window) {
        this(window, false);
	}
    
    public FavoritesMenu(IWorkbenchWindow window, boolean useExistingEditor){
        super(ModelServiceMessages.getString("MenuConst.F&avorites_8"));
        this._window = window;
        
        this.useExistingEditor = useExistingEditor;
        
        Map<String, String> favoritesMap = FavoritesUtil.getFavoritesMap();

        AddFavoritesAction favoritesAddAction = new AddFavoritesAction(window, favoritesMap);
        add(favoritesAddAction);
        favoritesAddAction.addFavoritesChangeListener(this);

        ArrangeFavoritesAction favoritesArrangeAction = new ArrangeFavoritesAction(this._window, favoritesMap);
        add(favoritesArrangeAction);
        favoritesArrangeAction.addFavoritesChangeListener(this);

        add(new Separator());

        updateAllFavoritesItems(favoritesMap);        
    }
    

	public void favoritesChanged(FavoritesChangeEvent fce) {
		updateAllFavoritesItems(fce.getFavoritesMap());
	}

	private void updateAllFavoritesItems(Map<String, String> favoritesMap) {
		Iterator<String> favoritesIt = favoritesMap.keySet().iterator();
		
		removeAllFavoritesItemAction();
		while (favoritesIt.hasNext()) {
			String name = (String) favoritesIt.next();
			String url = (String) favoritesMap.get(name);
			addFavoritesItemAction(name, url);
		}

		FavoritesUtil.saveFavoritesMap(favoritesMap);
	}

	private void removeAllFavoritesItemAction(){
		for(int i = 0; i < this._favoritesItemIdList.size(); i++){
			remove((String)this._favoritesItemIdList.get(i));
		}
		this._favoritesItemIdList.clear();
	}
	
	private void addFavoritesItemAction(String name, String url) {
		FavoritesItemAction favoritesItemAction = new FavoritesItemAction(this._window, name, url, useExistingEditor);
		add(favoritesItemAction);
		this._favoritesItemIdList.add(	FavoritesItemAction.ID + "_" + name);
	}

}
