###############################################################################
# Copyright (c) 2007 IBM Corporation and others.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#     Hisashi MIYASHITA - initial API and implementation
###############################################################################

BASEPKG = org.eclipse.actf.util.win32.comclutch
BASEIMPLPKG = $(BASEPKG).impl
JAVAH = "$(JAVA_HOME:/=\)\bin\javah"
JAVAH_FLAGS = -classpath ../bin
JAVA_INC = "$(JAVA_HOME)/include"
BLD = ..\build
CC = cl.exe
CFLAGS = -c -Zi -Ox -I $(JAVA_INC)
LINK = link.exe
LDFLAGS = -debug -dll
LIBS = User32.lib Oleacc.lib
CLASSES = $(BASEPKG).IResourceImpl 

OBJS = $(BLD)\Common.obj $(BLD)\IDispatch.obj $(BLD)\IUnknown.obj \
       $(BLD)\JCTypeBridge.obj $(BLD)\RefContainer.obj $(BLD)\Handler.obj \
       $(BLD)\IEnumUnknown.obj $(BLD)\IOleContainer.obj $(BLD)\IUrlHistoryStg2.obj \
       $(BLD)\JCTypeBridgeArray.obj $(BLD)\MSAA.obj $(BLD)\IServiceProvider.obj
       
DLL = ../JavaCom.dll

all: init headers $(DLL)

init:
	-mkdir $(BLD)

clean:
	-rd /S/Q $(BLD)

headers: 
	$(JAVAH) $(JAVAH_FLAGS) $(BASEPKG).ComService $(BASEPKG).RefContainer $(BASEPKG).Handler $(BASEIMPLPKG).IResourceImpl $(BASEIMPLPKG).IUnknownImpl $(BASEIMPLPKG).IDispatchImpl $(BASEIMPLPKG).IEnumUnknownImpl $(BASEIMPLPKG).IOleContainerImpl $(BASEIMPLPKG).IUrlHistoryStg2Impl $(BASEIMPLPKG).IServiceProviderImpl org.eclipse.actf.util.win32.msaa.MSAA

$(DLL): $(OBJS)
	$(LINK) $(LDFLAGS) $(OBJS) -out:$(DLL) $(LIBS)

.cpp{$(BLD)}.obj:
	$(CC) $(CFLAGS) -Fo$(BLD)\ $<


